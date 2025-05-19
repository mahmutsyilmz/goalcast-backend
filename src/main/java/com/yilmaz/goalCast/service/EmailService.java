package com.yilmaz.goalCast.service;

public interface EmailService {
    /**
     * Kullanıcıya e-posta doğrulama linki içeren bir e-posta gönderir.
     * @param toEmail Alıcı e-posta adresi.
     * @param username Kullanıcının adı (e-postada kişiselleştirme için).
     * @param verificationLink Doğrulama linki.
     */
    void sendEmailVerificationEmail(String toEmail, String username, String verificationLink);

    void sendNewMatchEmail(String toEmail, String username, String subject, String matchDetailsHtml, String matchLink);
    void sendPredictionResultEmail(String toEmail, String username, String subject, String resultDetailsHtml, String predictionsLink);
}