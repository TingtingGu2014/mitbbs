package com.bbs.mitbbs;


import com.bbs.mitbbs.exceptions.NullHttpDocumentException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
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
    public static final String TOPIC_ITEMS = "审,稿,评,review";
    
    private List<String> ids = new ArrayList<>();
    
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
//                Document doc = Jsoup.parse(con.getInputStream(), "GBK", url);
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
    
    public Document getHtmlDocument(){
        
        BufferedReader in = null;
        StringBuffer response = null;
        Document doc = null;
        
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
                doc = Jsoup.parse(con.getInputStream(), "GBK", url);
//                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } catch (IOException ex) {
                Logger.getLogger(WebCrower.class.getName()).log(Level.SEVERE, null, ex);
            }
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
        return doc;
    }
    
    public String[] getPostByAuthor(String author){
        String[] mailInfo = null;
        InputStream in = null;
        StringBuffer response = getHttpResponse();
        if(null != response){
            try{
                Document doc = getHtmlDocument();
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
                                if(null != user && user.equals(author)){
                                    while(!element.tagName().equals("td")){
                                        element = element.parent();
                                    }
                                    Element tr = element.parent();
                                    Element td2 = tr.child(2);
                                    Element postLink = td2.getElementsByTag("a").first();
                                    List childrenNodes = postLink.childNodes();
                                    if(null == childrenNodes || childrenNodes.isEmpty()){
                                        continue;
                                    }
                                    String link = postLink.attr("href");
                                    String postId = getPostIdByLink(link);
                                    if(ids.contains(postId)){
                                        continue;
                                    }
                                    ids.add(postId);
                                    String message = "link "+ postId+" is http://www.mitbbs.com"+link+ " posted by "+author;
                                    mailInfo = new String[2];
                                    mailInfo[0] = author;
                                    mailInfo[1] = message;
                                    System.out.println(message);
                                    EmailHandler.sendAwsEmail(mailInfo);
                                }

                            }                      
                        }
                    }
                }
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
            finally{
                if(null != in){
                    try{
                        in.close();
                    }
                    catch(IOException ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
        else{
            return mailInfo;
        }
        return mailInfo;
    }
    
    public String[] getPostByTopic(){
        String[] mailInfo = null;
        InputStream in = null;
        StringBuffer response = getHttpResponse();
        if(null != response){
            try{
                Document doc = getHtmlDocument();
                doc.outputSettings().prettyPrint(false);
                //get meta description content
                Elements metaElements = doc.select("a");

                int size = metaElements.size();
                if(size > 0){
                    for(Element element : metaElements) {
                        if(null != element && element.hasAttr("href") && null != element.tagName()) {
                            while(null != element && null != element.tagName() && !element.tagName().equals("td")){
                                element = element.parent();
                            }
                            if(null == element){
                                continue;
                            }
                            Element tr = element.parent();
                            Elements trChildren = tr.children();
                            if(null == trChildren || trChildren.size() < 6){
                                continue;
                            }
                            Element td2 = trChildren.get(2);
                            Element postLink = td2.getElementsByTag("a").first();
                            if(null == postLink){
                                continue;
                            }
                            List childrenNodes = postLink.childNodes();
                            if(null == childrenNodes || childrenNodes.isEmpty()){
                                continue;
                            }
                            String text = postLink.childNodes().get(0).outerHtml().replace("\n", "").replace("\r", "").trim();
                            boolean hasTopicItems = searchByTopic(text);
                            if(false == hasTopicItems){
                                continue;
                            }
                            String link = postLink.attr("href");
                            String postId = getPostIdByLink(link);
                            if(ids.contains(postId)){
                                continue;
                            }
                            ids.add(postId);
                            String poster = "unknown author";
                            String replier = "unknown replier";
                            Element td4 = trChildren.get(4);
                            Node authorNode = td4.childNodes().get(0);
                            String userInfo = authorNode.childNode(0).outerHtml();
                            if(null != userInfo && !userInfo.equals("")){
                                poster = userInfo;
                            }
                            
                            Element td5 = trChildren.get(5);
                            Node replierNode = td4.childNodes().get(0);
                            String replierInfo = replierNode.childNode(0).outerHtml();
                            if(null != replierInfo && !replierInfo.equals("")){
                                replier = replierInfo;
                            }
                            
                            String message = "link "+ postId+" is http://www.mitbbs.com"+link+ " posted by "+poster;
                            mailInfo = new String[2];
                            mailInfo[0] = poster+" posted: "+text;
                            mailInfo[1] = message;
                            System.out.println(message);
                            EmailHandler.sendAwsEmail(mailInfo);                
                        }
                    }
                }
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
            finally{
                if(null != in){
                    try{
                        in.close();
                    }
                    catch(IOException ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
        else{
            return mailInfo;
        }
        return mailInfo;
    }
    
    public boolean searchByTopic(String text){
        boolean found = false;
        String[] topicItems = TOPIC_ITEMS.split(",");
        for(String item : topicItems){
            if(text.contains(item)){
                found = true;
                return found;
            }
        }
        return found;
    }
    
    public String getPostIdByLink(String link){
        // link = "/article_t/Immigration/33777501.html;
    	if(null == link){
    		return null;
    	}
        String[] linkInfo = link.split("/");
        if(null != linkInfo || linkInfo.length == 3){
            return linkInfo[linkInfo.length-1].split("\\.")[0];
        }
        else{
            return null;
        }
    }
}
