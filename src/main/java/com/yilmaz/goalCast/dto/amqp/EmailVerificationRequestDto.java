package com.yilmaz.goalCast.dto.amqp; // Kendi paket yapına göre ayarla


public class EmailVerificationRequestDto {
    private Long userId;
    private String userEmail;
    private String username;

    // No-args constructor Jackson için önemli
    public EmailVerificationRequestDto() {
    }

    public EmailVerificationRequestDto(Long userId, String userEmail, String username) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.username = username;
    }

    // Getters
    public Long getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUsername() {
        return username;
    }

    // Setters (Jackson için veya manuel oluşturma için gerekebilir)
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "EmailVerificationRequestDto{" +
               "userId=" + userId +
               ", userEmail='" + userEmail + '\'' +
               ", username='" + username + '\'' +
               '}';
    }
}