package com.MyWeb.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final JavaMailSender javaMailSender;

//    private static final String sendEmail = "taeyeonimda@gmail.com";

    @Value("${mail.myMail}")
    private static String sendEmail;

    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public MimeMessage CreateMail(String mail){
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(mail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("이메일 인증");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + 123441 + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    public void sendMail(String findEmail) {
        MimeMessage message = CreateMail(findEmail);
        javaMailSender.send(message);
    }
}
