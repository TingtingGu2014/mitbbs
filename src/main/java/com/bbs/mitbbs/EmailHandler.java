/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbs.mitbbs;

import com.solomon.aws.service.AwsMailServ;
import com.solomon.aws.service.AwsUtil;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 *
 * @author arronzhao
 */
public class EmailHandler {
    public static void sendEmail(String author, String content){
        String to = "gttzt2004@yahoo.com";

        String from = "ztgtt2004@gmail.com";

        String host = "localhost";

        Properties properties = System.getProperties();

        properties.setProperty("mail.smtp.host", host);

        Session session = Session.getDefaultInstance(properties);

        try{
           MimeMessage message = new MimeMessage(session);

           message.setFrom(new InternetAddress(from));

           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

           message.setSubject(author+" just posted at mitbbs");

           message.setText(content);

           Transport.send(message);
           System.out.println("Sent message successfully....");
        }catch (MessagingException mex) {
           mex.printStackTrace();
        }
    }
    
    public static void sendAwsEmail(String[] mailInfo){
        AwsMailServ mailServ = AwsUtil.getAwsMailServ();
        mailServ.setFrom(AwsUtil.FROM);
        mailServ.setTo(AwsUtil.TO);
        mailServ.setTopic(mailInfo[0]);
        mailServ.setBody(mailInfo[1]);
        mailServ.sendMail();
    }
}
