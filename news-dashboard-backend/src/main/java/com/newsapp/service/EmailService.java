package com.newsapp.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSummaryEmail(String toEmail, String articleTitle, String aiSummary, String articleUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Intel Briefing: " + articleTitle);
        
        String body = "Here is your requested AI Summary:\n\n" +
                      aiSummary + "\n\n" +
                      "Read the full article here: " + articleUrl + "\n\n" +
                      "Powered by News & Opportunities Dashboard";
                      
        message.setText(body);
        
        try {
            mailSender.send(message);
            System.out.println("Email successfully sent to " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send email. Ensure EMAIL_USER and EMAIL_PASS are set correctly. Error: " + e.getMessage());
        }
    }
}
