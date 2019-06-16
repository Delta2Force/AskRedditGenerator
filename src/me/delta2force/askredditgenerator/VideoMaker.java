package me.delta2force.askredditgenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.tritonus.share.sampled.file.TAudioFileFormat;

import com.google.common.io.Files;

public class VideoMaker {
	public static ArrayList<File> filesInOrder;
	public static int imageStuff = 0;
	
	public static void makeVideo() {
		if(!new File("render").exists()) {
			new File("render").mkdir();
		}
		
		filesInOrder = new ArrayList<>();
		gatherFiles();
		stitchAudio();
		makeTitle();
		makeComments(); 
		pngtoavi();
		removeResidue();
		System.out.println("Your video is done!");
		System.exit(0);
	}
	
	public static void removeResidue() {
		System.out.println("Removing files...");
		for(File f : new File(".").listFiles()) {
			if(f.getName().startsWith(AskRedditGenerator.SUBMISSIONID) && !f.getName().endsWith(".avi")) {
				f.delete();
			}
		}
		for(File f : new File("render").listFiles()) {
			f.delete();
		}
		
		new File("render").delete();
	}
	
	public static void gatherFiles() {
		for(File f : new File(".").listFiles()) {
			if(f.getName().startsWith(AskRedditGenerator.SUBMISSIONID + ".com") && !f.getName().endsWith(".png")) {
				filesInOrder.add(f);
				System.out.println("added " + f.getName());
			}
		}
	}
	
	public static void stitchAudio() {
        try {
        	Vector<FileInputStream> ais = new Vector<FileInputStream>();
        	
        	ais.add(new FileInputStream(new File(AskRedditGenerator.SUBMISSIONID + ".greeting.wav")));
        	ais.add(new FileInputStream(new File(AskRedditGenerator.SUBMISSIONID + ".title.wav")));
        	for(File f : filesInOrder) {
        		ais.add(new FileInputStream(f));
        	}

           SequenceInputStream sis = new SequenceInputStream(ais.elements());
           FileOutputStream fostream = new FileOutputStream(new File(AskRedditGenerator.SUBMISSIONID + ".audio.wav"));//destinationfile

           int temp;

           while( ( temp = sis.read() ) != -1)
           {
               fostream.write(temp);
           }
           fostream.close();
           sis.close();
           for(FileInputStream fis : ais) {
        	   fis.close();
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void pngtoavi() {
		System.out.println("FFMPEG is working...");
		try {
			String message = "";
			if(OsCheck.getOperatingSystemType().equals(OsCheck.OSType.Windows)) {
			    message = "ffmpeg.exe -y -framerate 30 -i render/%04d.png -i " + AskRedditGenerator.SUBMISSIONID + ".audio.wav -q:v 1 -v quiet " + AskRedditGenerator.SUBMISSIONID + ".avi";
			}else if(OsCheck.getOperatingSystemType().equals(OsCheck.OSType.Linux)) {
			    message = "ffmpeg -y -framerate 30 -i render/%04d.png -i " + AskRedditGenerator.SUBMISSIONID + ".audio.wav -q:v 1 -v quiet " + AskRedditGenerator.SUBMISSIONID + ".avi";
			}
			Process child = Runtime.getRuntime().exec(message);
		    child.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("FFMPEG is done!");
	}
	
	public static void makeTitle() {
		int ms = getMSLengthOfSoundFile(new File(AskRedditGenerator.SUBMISSIONID + ".title.wav"));
		ms+=getMSLengthOfSoundFile(new File(AskRedditGenerator.SUBMISSIONID + ".greeting.wav"));
		
		int msperframe = 1000/30;
		int lng = ms/msperframe;
		
		try {
			for(int i = 0;i<lng;i++) {
				Files.copy(new File(AskRedditGenerator.SUBMISSIONID + ".title.png"), new File("render/"+String.format("%04d", imageStuff) + ".png"));
				imageStuff++;
				System.out.println("Rendered frame " + imageStuff + " (Title)"); 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void makeComments() {
		for(File f : filesInOrder) {
			int ms = getMSLengthOfSoundFile(f);
			
			int msperframe = 1000/30;
			int lng = ms/msperframe;
			
			try {
				for(int i = 0;i<lng;i++) {
					Files.copy(new File(f.getName().replace(".wav", ".png")), new File("render/"+String.format("%04d", imageStuff) + ".png"));
					imageStuff++;
					System.out.println("Rendered frame " + imageStuff + " (Comment)"); 
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static int getMSLengthOfSoundFile(File f) {
		AudioFileFormat fileFormat = null;
		try {
			fileFormat = AudioSystem.getAudioFileFormat(f);
		} catch (UnsupportedAudioFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    if (fileFormat instanceof TAudioFileFormat) {
	        Map<?, ?> properties = ((TAudioFileFormat) fileFormat).properties();
	        String key = "duration";
	        Long microseconds = (Long) properties.get(key);
	        return (int) (microseconds / 1000);
	        //int sec = (mili / 1000) % 60;
	        //int min = (mili / 1000) / 60;
	        //System.out.println("time = " + min + ":" + sec);
	    } else {
	        try {
				throw new UnsupportedAudioFileException();
			} catch (UnsupportedAudioFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    return 0;
	}
	
	static class OsCheck {
		  /**
		   * types of Operating Systems
		   */
		  public enum OSType {
		    Windows, MacOS, Linux, Other
		  };

		  // cached result of OS detection
		  protected static OSType detectedOS;

		  /**
		   * detect the operating system from the os.name System property and cache
		   * the result
		   * 
		   * @returns - the operating system detected
		   */
		  public static OSType getOperatingSystemType() {
		    if (detectedOS == null) {
		      String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		      if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
		        detectedOS = OSType.MacOS;
		      } else if (OS.indexOf("win") >= 0) {
		        detectedOS = OSType.Windows;
		      } else if (OS.indexOf("nux") >= 0) {
		        detectedOS = OSType.Linux;
		      } else {
		        detectedOS = OSType.Other;
		      }
		    }
		    return detectedOS;
		  }
		}
}
