package com.E_Commerce.User_services.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SendEmailServices {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${app.gateway.url}")
    private String gatewayUrl;

    public SendEmailServices(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerifiedEmail(String email, String verifiedToken){
        String subJect = "Email de verificacion";
        String path = "/e-commerce/api/v1/auth/verified-user";
        String messege = "Clic en el boton o copia el link";

        sendEmail(email, verifiedToken, subJect, path, messege);
    }

    public void sendResetEmail(String email, String verifiedToken){
        String subJect = "Email de recuperacion";
        String path = "/e-commerce/api/v1/auth/verified-resettoken";
        String messege = "Clic en el boton o copia el link";

        sendEmail(email, verifiedToken, subJect, path, messege);
    }

    public void sendEmail(String email,String token, String subject, String path, String messege){
        try{
            String actionUrl = UriComponentsBuilder.fromHttpUrl(gatewayUrl)
                    .path(path)
                    .queryParam("token", token)
                    .queryParam("email", email)
                    .toUriString();

            String content = """
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border-radius: 8px; background-color: #f9f9f9; text-align: center;">
                        <h2 style="color: #333;">%s</h2>
                        <p style="font-size: 16px; color: #555;">%s</p>
                        <a href="%s" style="display: inline-block; margin: 20px 0; padding: 10px 20px; font-size: 16px; color: #fff; background-color: #007bff; text-decoration: none; border-radius: 5px;">Proceed</a>
                        <p style="font-size: 14px; color: #777;">Or copy and paste this link into your browser:</p>
                        <p style="font-size: 14px; color: #007bff;">%s</p>
                        <p style="font-size: 12px; color: #aaa;">This is an automated message. Please do not reply.</p>
                    </div>
                """.formatted(subject, messege, actionUrl, actionUrl);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setFrom(from);
            helper.setText(content, true);
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
