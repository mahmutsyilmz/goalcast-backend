package com.yilmaz.goalCast.service.impl;

import com.yilmaz.goalCast.service.EmailService;
import jakarta.mail.MessagingException; // jakarta.mail.MessagingException kullanalım
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmailAddress;

    @Value("${spring.application.name}") // appName'i doğru yerden alıyoruz
    private String appName;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async // Asenkron çalışması için
    public void sendEmailVerificationEmail(String toEmail, String username, String verificationLink) {
        logger.info("Attempting to send verification email to: {}", toEmail);
        if (toEmail == null || toEmail.trim().isEmpty()) {
            logger.warn("Cannot send verification email: toEmail is null or empty.");
            return;
        }
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            // true: multipart message (HTML için gerekli), "utf-8": encoding
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

            String htmlMsg = String.format("""
                <h3>%s'a Hoş Geldin, %s!</h3>
                <p>Kaydolduğun için teşekkürler. Lütfen aşağıdaki bağlantıya tıklayarak e-posta adresini doğrula:</p>
                <p><a href="%s" style="padding: 10px 15px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px;">E-postayı Doğrula</a></p>
                <p>Eğer bir hesap oluşturmadıysan, bu e-postayı dikkate almayabilirsin.</p>
                <br/>
                <p>Teşekkürler,<br/>%s Ekibi</p>
                """, appName, username, verificationLink, appName);

            helper.setText(htmlMsg, true); // true: HTML content
            helper.setTo(toEmail);
            helper.setSubject(String.format("E-posta Adresini Doğrula - %s", appName));
            helper.setFrom(fromEmailAddress);

            mailSender.send(mimeMessage);
            logger.info("Verification email sent successfully to: {}", toEmail);

        } catch (MessagingException e) { // MimeMessageHelper oluştururken veya setText, setTo vb. hatalar için
            logger.error("MessagingException during email preparation for verification to [{}]: {}", toEmail, e.getMessage(), e);
        } catch (MailException e) { // Mail sunucusuna bağlanma, gönderme hataları için (daha genel)
            logger.error("MailException while sending verification email to [{}]: {}", toEmail, e.getMessage(), e);
        } catch (Exception e) { // Beklenmedik diğer tüm hatalar
            logger.error("An unexpected error occurred while attempting to send verification email to [{}]: {}", toEmail, e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendNewMatchEmail(String toEmail, String username, String subject, String matchDetailsHtml, String matchLink) {
        logger.info("Attempting to send new match notification email to: {}", toEmail);
        if (toEmail == null || toEmail.trim().isEmpty()) {
            logger.warn("Cannot send new match email: toEmail is null or empty.");
            return;
        }
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

            String htmlMsg = String.format("""
            <h3>Merhaba %s,</h3>
            <p>%s</p>
            <p><a href="%s" style="padding: 10px 15px; background-color: #0d6efd; color: white; text-decoration: none; border-radius: 5px;">Maçı Görüntüle ve Tahmin Yap</a></p>
            <br/>
            <p>İyi şanslar,<br/>%s Ekibi</p>
            """, username, matchDetailsHtml, matchLink, appName);

            helper.setText(htmlMsg, true);
            helper.setTo(toEmail);
            helper.setSubject(subject); // Subject dışarıdan geliyor, bu iyi.
            helper.setFrom(fromEmailAddress);

            mailSender.send(mimeMessage);
            logger.info("New match notification email sent successfully to: {}", toEmail);

        } catch (MessagingException e) {
            logger.error("MessagingException during new match email preparation for [{}]: {}", toEmail, e.getMessage(), e);
        } catch (MailException e) {
            logger.error("MailException while sending new match email to [{}]: {}", toEmail, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error sending new match email to [{}]: {}", toEmail, e.getMessage(), e);
        }
    }

    // YENİ EKLENEN METOT
    @Override
    @Async
    public void sendPredictionResultEmail(String toEmail, String username, String subject, String resultDetailsHtml, String predictionsLink) {
        logger.info("Attempting to send prediction result email to: {}", toEmail);
        if (toEmail == null || toEmail.trim().isEmpty()) {
            logger.warn("Cannot send prediction result email: toEmail is null or empty.");
            return;
        }
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

            String htmlMsg = String.format("""
                <h3>Merhaba %s,</h3>
                <p>Bir tahminin sonuçlandı:</p>
                <div style="padding: 10px; border: 1px solid #ddd; border-radius: 5px; margin-bottom: 15px; background-color: #f9f9f9;">
                    %s
                </div>
                <p><a href="%s" style="padding: 10px 15px; background-color: #198754; color: white; text-decoration: none; border-radius: 5px;">Tüm Tahminlerini Gör</a></p>
                <br/>
                <p>Teşekkürler,<br/>%s Ekibi</p>
                """, username, resultDetailsHtml, predictionsLink, appName);

            helper.setText(htmlMsg, true);
            helper.setTo(toEmail);
            helper.setSubject(subject); // Subject dışarıdan geliyor.
            helper.setFrom(fromEmailAddress);

            mailSender.send(mimeMessage);
            logger.info("Prediction result email sent successfully to: {}", toEmail);

        } catch (MessagingException e) {
            logger.error("MessagingException during prediction result email preparation for [{}]: {}", toEmail, e.getMessage(), e);
        } catch (MailException e) {
            logger.error("MailException while sending prediction result email to [{}]: {}", toEmail, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error sending prediction result email to [{}]: {}", toEmail, e.getMessage(), e);
        }
    }
}