package com.yilmaz.goalCast.service; // Kendi paket yapına göre ayarla

import com.yilmaz.goalCast.dto.amqp.NewMatchNotificationDto;
import com.yilmaz.goalCast.dto.amqp.PredictionResultNotificationDto;
import com.yilmaz.goalCast.model.User;

public interface NotificationService {
    void sendEmailVerificationRequest(User user);
    void sendNewMatchAddedNotificationRequest(NewMatchNotificationDto newMatchDto);
    void sendPredictionResultNotificationRequest(PredictionResultNotificationDto predictionResultDto);

    // İleride eklenecek diğer bildirim metotları
    // void sendNewMatchNotification(Match match, List<User> subscribers);
    // void sendPredictionResultNotification(User user, Prediction prediction);
}