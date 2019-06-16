package me.delta2force.askredditgenerator;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;

import javax.imageio.ImageIO;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Comment;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.references.SubmissionReference;
import net.dean.jraw.tree.CommentNode;

public class AskRedditGenerator {
	public static String GREETING = "";
	public static String SUBMISSIONID = "bwi25o";
	public static String TITLE ="";
	public static String SUBMISSIONURL ="";
	
	public static void main(String[] args) throws Exception{
		GREETING = "This is the default greeting. Please create a greeting.txt.";
		if(args.length > 0) {
			SUBMISSIONID = args[0];
		}
		try {
			BufferedReader greetingReader = new BufferedReader(new FileReader(new File("greeting.txt")));
			GREETING = greetingReader.readLine();
			greetingReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		UserAgent userAgent = new UserAgent("bot", "my.cool.bot", "1.0.0", "askreddit");
		RedditClient reddit = OAuthHelper.automatic(new OkHttpNetworkAdapter(userAgent), Credentials.userlessApp("arrhJA7RfcSWkQ", UUID.randomUUID()));

		SubmissionReference rcn = reddit.submission(SUBMISSIONID);
		TITLE=rcn.inspect().getTitle();
		SUBMISSIONURL=rcn.inspect().getUrl();
		System.out.println("Using post: '"+TITLE+"'! " + rcn.inspect().getUrl());
		
		GoogleTextToSpeech gtts = new GoogleTextToSpeech();
		InputStream is = gtts.say(GREETING + ".", "en");
		Files.copy(is, new File(SUBMISSIONID + ".greeting.wav").toPath());
		InputStream si = gtts.say(TITLE, "en");
		Files.copy(si, new File(SUBMISSIONID + ".title.wav").toPath());
		ImageIO.write(TitleRenderer.renderTitle(TITLE), "PNG", new FileOutputStream(new File(SUBMISSIONID + ".title.png")));
		
		int maxlength = 100;
		Iterator<CommentNode<Comment>> comms = rcn.comments().iterator();
		
		try {
			for(int i = 0;i<maxlength;i++) {
				Comment c = comms.next().getSubject();
				if(c.getBody().length()>100) {
					i--;
				}else {
					ImageIO.write(CommentRenderer.renderComment(c), "PNG", new FileOutputStream(new File(SUBMISSIONID + ".com"+i + ".png")));
					InputStream es = gtts.say(c.getBody(), "en");
					Files.copy(es, new File(SUBMISSIONID + ".com"+i+".wav").toPath());
					System.out.println("Done with comment " + i + "!");
				}
			}
		}catch(NoSuchElementException ex) {
			ex.printStackTrace();
		}
		
		VideoMaker.makeVideo();
	}
	
	static class TitleRenderer{
		public static BufferedImage renderTitle(String title) {
			Font f = null;
			try {
				f = Font.createFont(Font.TRUETYPE_FONT, new File("NotoSans-Regular.ttf"));
			} catch (FontFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Font render = f.deriveFont(20f);
			Canvas c = new Canvas();
			BufferedImage b = new BufferedImage(c.getFontMetrics(render).stringWidth(title), c.getFontMetrics(render).getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = b.createGraphics();
			g2.setFont(render);
			g2.drawString(title,0,20);
			BufferedImage bi = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = bi.createGraphics();
			g2d.setColor(new Color(26,26,27));
			g2d.fillRect(0, 0, 1280, 720);
			g2d.drawImage(b, (1280/2)-(b.getWidth()/2), (720/2)-(b.getHeight()/2), null);
			return bi;
		}
	}
}
