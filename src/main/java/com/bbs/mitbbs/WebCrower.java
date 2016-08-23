package com.bbs.mitbbs;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author arronzhao
 */
public class WebCrower {
    
    public static final String USER_AGENT = "Mozilla/5.0";
    public static final String url = "http://www.mitbbs.com/bbsdoc/Immigration.html";
    
    public StringBuffer getHttpResponse(){
        
        BufferedReader in = null;
        StringBuffer response = null;
        
        HttpURLConnection con = null;
    
        try{
            URL obj = null;
            try {
                obj = new URL(url);
            } catch (MalformedURLException ex) {
                Logger.getLogger(WebCrower.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            try {
                con = (HttpURLConnection) obj.openConnection();
            } catch (IOException ex) {
                Logger.getLogger(WebCrower.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                // optional default is GET
                con.setRequestMethod("GET");
            } catch (ProtocolException ex) {
                Logger.getLogger(WebCrower.class.getName()).log(Level.SEVERE, null, ex);
            }

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = -1;
            try {
                responseCode = con.getResponseCode();
            } catch (IOException ex) {
                Logger.getLogger(WebCrower.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            
            try {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } catch (IOException ex) {
                Logger.getLogger(WebCrower.class.getName()).log(Level.SEVERE, null, ex);
            }
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
            }
            in.close();
        }
        catch(Exception ex){
            Logger.getLogger(WebCrower.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            if(null != con){
                con.disconnect();
            }
            if(null != in){
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(WebCrower.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return response;
    }
    
    public String[] getPostByAuthor(String author){
        StringBuffer response = getHttpResponse();
        if(null != response){
            String httpDoc = response.toString();
//            System.out.println(httpDoc);
            Document doc = Jsoup.parse(httpDoc);
            doc.outputSettings().prettyPrint(false);
            //get meta description content
            Elements metaElements = doc.select("a");
            
            int size = metaElements.size();
            if(size > 0){
                for(Element element : metaElements) {
                    if(element.hasAttr("href")) {
                        String href = element.attr("href");
                        if(href.startsWith("/user_info/")){
                            String user = element.text();
                            if(null != user && user.equals("TinyYaya")){
                                while(!element.tagName().equals("td")){
                                    element = element.parent();
                                }
                                Element tr = element.parent();
                                Element td1 = tr.child(2);
                                Element postLink = td1.getElementsByTag("a").first();
                                String link = postLink.attr("href");
                                String message = "link is "+link+ " with topic = 我们 的"+postLink.text();
                                EmailHandler.sendEmail(message);
                            }
                            
                        }                      
                    }
                }
            }
        }
        else{
            return null;
        }
        return null;
    }
}
