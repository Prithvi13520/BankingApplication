package com.app.banking.service.impl;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import com.app.banking.dto.EmailDetails;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService{

   private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

   @Autowired
   private JavaMailSender javaMailSender;

   @Value("${spring.mail.username}")
   private String senderEmail;

   @Value("${custom.mail.enable}")
   private String mailEnable;

@Override
@Async
public void sendEmailAlert(EmailDetails emailDetails) {

if(mailEnable.equalsIgnoreCase("true")){
    logger.info("sendEmailAlert started successfully");
    try{
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(senderEmail);
        mailMessage.setTo(emailDetails.getRecipient());
        mailMessage.setText(emailDetails.getMessageBody());
        mailMessage.setSubject(emailDetails.getSubject());

        javaMailSender.send(mailMessage);
        logger.info("Mail sent successfully");
    }
    catch(RuntimeException e)
    {
        logger.error("Mail not sent because we have a problem "+e.getMessage());
    }

}
else{
    logger.info("mail communication is not active as because custom.mail.enable variable is "+mailEnable);
}

}

@Override
@Async
public void sendEmailWithAttachment(EmailDetails emailDetails) {

    if(mailEnable.equalsIgnoreCase("true")){
        logger.info("sendEmailwithAttachment started successfully");
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try{
            mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
            mimeMessageHelper.setFrom(senderEmail);
            mimeMessageHelper.setTo(emailDetails.getRecipient());
            mimeMessageHelper.setText(emailDetails.getMessageBody());
            mimeMessageHelper.setSubject(emailDetails.getSubject());

            FileSystemResource file = new FileSystemResource(new File(emailDetails.getAttachment()));
            mimeMessageHelper.addAttachment(file.getFilename(), file);
            javaMailSender.send(mimeMessage);
            logger.info("Mail sent successfully");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            logger.error("Mail not sent because we have a problem "+e.getMessage());
        }

    }
    else{
        logger.info("mail communication is not active as because custom.mail.enable variable is "+mailEnable);
    }
    
}

}
