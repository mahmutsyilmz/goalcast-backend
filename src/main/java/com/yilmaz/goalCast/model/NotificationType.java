package com.yilmaz.goalCast.model;

public enum NotificationType {
    NEW_MATCH_ADDED,          // Admin yeni bir maç eklediğinde
    PREDICTION_RESULT_WIN,    // Kullanıcının tahmini kazandığında
    PREDICTION_RESULT_LOSS,   // Kullanıcının tahmini kaybettiğinde
    PREDICTION_RESULT_DRAW,   // Kullanıcının tahmini berabere bittiğinde (puan iadesi vb.)
    EMAIL_VERIFIED,           // E-posta başarıyla doğrulandığında (opsiyonel)
    // Gelecekte eklenebilecekler:
    // NEW_LEAGUE_ADDED,
    // FRIEND_REQUEST,
    // MATCH_REMINDER
}