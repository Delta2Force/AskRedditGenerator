package me.delta2force.askredditgenerator;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.dean.jraw.models.Comment;

public class CommentRenderer {
	public static BufferedImage renderComment(Comment com) {
		String author = com.getAuthor();
		String message = com.getBody();
		Canvas c = new Canvas();
		Font f = null;
		try {
			f = Font.createFont(Font.TRUETYPE_FONT, new File("NotoSans-Regular.ttf"));
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<String> msgs = (ArrayList<String>) StringUtils.wrap(message,c.getFontMetrics(f.deriveFont(14f)),833-10);
		BufferedImage b = new BufferedImage(850, 14 + (msgs.size() * c.getFontMetrics(f.deriveFont(14f)).getHeight()), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = b.createGraphics();
		g.setColor(new Color(26,26,27));
		g.fillRect(0, 0, b.getWidth(), b.getHeight());
		g.setFont(f.deriveFont(12f));
		g.setColor(new Color(79, 188, 255));
		g.drawString(author, 8, 10);
		g.setColor(new Color(129,131,132));
		g.drawString(com.getScore() + " points", g.getFontMetrics().stringWidth(author)+16, 9);
		g.setFont(f.deriveFont(14f));
		g.setColor(Color.WHITE);
		int i = 1;
		for(String s : msgs) {
			g.drawString(s, 8, 10+(g.getFontMetrics().getHeight()*i));
			i++;
		}
		BufferedImage bi = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		g2d.setColor(new Color(26,26,27));
		g2d.fillRect(0, 0, 1280, 720);
		g2d.drawImage(b, (1280/2)-(b.getWidth()/2), (720/2)-(b.getHeight()/2), null);
		return bi;
	}
}
