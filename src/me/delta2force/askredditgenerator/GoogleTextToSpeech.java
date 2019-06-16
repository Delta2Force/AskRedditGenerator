package me.delta2force.askredditgenerator;

import java.io.InputStream; // import  classes from Java library
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class GoogleTextToSpeech {
  private static String ENCODING = "UTF-8"; //make constants and assign values to them
  private static String URL_BEGINNING = "http://translate.google.com/translate_tts?client=tw-ob&ie=";
  private static String URL_QUERY = "&q=";
  private static String URL_TL = "&tl=";
  private static String USER_AGENT_LITERAL = "User-Agent";
  private static String USER_AGENT = "Mozilla/4.7";

  public InputStream say( String phrase, String lang) {

    try {
      //Make full URL
      phrase=URLEncoder.encode(phrase, ENCODING); //assign value to variable with name 'phrase' by use method encode from class URLEncoder 
      String sURL = URL_BEGINNING + ENCODING + URL_TL + lang + URL_QUERY + phrase; //assign value to variable sURL 
          URL url = new URL(sURL); // make instance url with constructor

          //Create connection
          URLConnection urlConn = url.openConnection(); //assign value to variable urlConn 
          HttpURLConnection httpUrlConn = (HttpURLConnection) urlConn; //Declaring  httpUrlConn var of type HttpURLConnection, assigning it  value to  var urlConn (reduce to  HttpURLConnection)
          httpUrlConn.addRequestProperty(USER_AGENT_LITERAL, USER_AGENT);// use method 

          //Create stream
          return urlConn.getInputStream();//create instance and assign it a value
    }
      //use exception with name ex
      catch (Exception ex) { 
      ex.printStackTrace(); //use method
    }
    return null;
  }
}