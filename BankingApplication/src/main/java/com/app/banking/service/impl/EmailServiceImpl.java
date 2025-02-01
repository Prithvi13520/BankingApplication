package com.app.banking.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import com.app.banking.dto.EmailDetails;

@Service
public class EmailServiceImpl implements EmailService{

   private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

   @Autowired
   private JavaMailSender javaMailSender;

   @Value("${spring.mail.username}")
   private String senderEmail;

@Override
@Async
public void sendEmailAlert(EmailDetails emailDetails) {
    
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
        logger.info("Mail not sent because we have a problem "+e.getMessage());
    }
}

}
