package com.yilmaz.goalCast.messaging;

import com.yilmaz.goalCast.config.RabbitMQConfig;
import com.yilmaz.goalCast.dto.amqp.NewMatchNotificationDto;
import com.yilmaz.goalCast.dto.amqp.PredictionResultNotificationDto;
import com.yilmaz.goalCast.model.Notification;
import com.yilmaz.goalCast.model.NotificationType;
import com.yilmaz.goalCast.model.User;
import com.yilmaz.goalCast.repository.NotificationRepository;
import com.yilmaz.goalCast.repository.UserRepository;
import com.yilmaz.goalCast.service.EmailService; // EmailService'i import et
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // frontendUrl için
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class NotificationEventListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationEventListener.class);
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService; // EmailService'i inject et

    @Value("${app.frontend.url}") // application.properties'ten frontend URL'ini al
    private String frontendUrl;

    @Autowired
    public NotificationEventListener(NotificationRepository notificationRepository,
                                     UserRepository userRepository,
                                     EmailService emailService) { // Constructor'a ekle
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConfig.NEW_MATCH_NOTIFICATION_QUEUE_NAME)
    @Transactional
    public void handleNewMatchNotification(NewMatchNotificationDto dto) {
        logger.info("Received new match notification request from RabbitMQ: matchId={}, sendEmailToAll={}", dto.getMatchId(), dto.isSendEmailToAll());
        if (dto == null || dto.getMatchId() == null) {
            logger.error("Received null or invalid NewMatchNotificationDto from RabbitMQ.");
            return;
        }

        try {
            // Kime site içi bildirim gönderilecek?
            // Şimdilik tüm kullanıcılara site içi bildirim gönderiyoruz.
            // İleride bu, kullanıcının lig takibi veya bildirim ayarlarına göre filtrelenebilir.
            List<User> targetSiteNotificationUsers = userRepository.findAll(); // DİKKAT: Performans!

            if (!targetSiteNotificationUsers.isEmpty()) {
                String siteMessage = String.format("%s ligine yeni bir maç eklendi: %s vs %s, başlama tarihi %s.",
                        dto.getLeagueName(),
                        dto.getHomeTeam(),
                        dto.getAwayTeam(),
                        dto.getMatchDate() != null ? dto.getMatchDate().format(DTF) : "yakın bir tarih"
                );
                String siteLink = String.format("%s/#/matches?matchId=%d", frontendUrl, dto.getMatchId());

                for (User user : targetSiteNotificationUsers) {
                    Notification notification = new Notification(user, siteMessage, NotificationType.NEW_MATCH_ADDED, siteLink, dto.getMatchId());
                    notificationRepository.save(notification);
                }
                logger.info("New match site notifications created for {} users regarding matchId={}", targetSiteNotificationUsers.size(), dto.getMatchId());
            } else {
                logger.info("No target users found for new match site notification (matchId: {}).", dto.getMatchId());
            }

            // Admin "Herkese E-posta Gönder" seçeneğini işaretlediyse e-posta gönder
            if (dto.isSendEmailToAll()) {
                logger.info("Flag 'sendEmailToAll' is true for matchId={}. Preparing to send emails.", dto.getMatchId());
                List<User> emailTargetUsers = userRepository.findAllByEmailIsNotNullAndEmailVerifiedTrue(); // Sadece e-postası olan ve doğrulanmış kullanıcılara
                // Veya tüm kullanıcılara (findAll()) gönderip EmailService içinde e-posta var mı kontrolü yapılabilir.
                // Şimdilik e-postası olan ve doğrulanmış olanlara gönderelim.

                if (!emailTargetUsers.isEmpty()) {
                    String emailSubject = String.format("Yeni Maç Eklendi: %s vs %s", dto.getHomeTeam(), dto.getAwayTeam());
                    String emailMatchDetailsHtml = String.format(
                            "%s liginde yeni bir maç eklendi: <strong>%s vs %s</strong>.<br>" +
                                    "Maç Tarihi: %s.<br>" +
                                    "Hemen tahminini yap!",
                            dto.getLeagueName(), dto.getHomeTeam(), dto.getAwayTeam(),
                            dto.getMatchDate() != null ? dto.getMatchDate().format(DTF) : "yakın bir tarih"
                    );                            // veya dto.getMatchDate() doğrudan kullanılmalı
                    String emailMatchLink = String.format("%s/#/matches?matchId=%d", frontendUrl, dto.getMatchId());

                    for (User user : emailTargetUsers) {
                        // İleride kullanıcının "yeni maç e-postası alma" tercihi kontrol edilebilir.
                        emailService.sendNewMatchEmail(user.getEmail(), user.getUsername(), emailSubject, emailMatchDetailsHtml, emailMatchLink);
                    }
                    logger.info("New match email sending process initiated for {} users regarding matchId={}", emailTargetUsers.size(), dto.getMatchId());
                } else {
                    logger.info("No users with verified email found to send new match email for matchId={}", dto.getMatchId());
                }
            }

        } catch (Exception e) {
            logger.error("Error processing new match notification for matchId {}: {}", dto.getMatchId(), e.getMessage(), e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PREDICTION_RESULT_NOTIFICATION_QUEUE_NAME)
    @Transactional
    public void handlePredictionResultNotification(PredictionResultNotificationDto dto) {
        logger.info("Received prediction result notification request from RabbitMQ: userId={}, matchId={}",
                dto.getUserId(), dto.getMatchId());

        if (dto == null || dto.getUserId() == null || dto.getMatchId() == null) {
            logger.error("Received null or invalid PredictionResultNotificationDto from RabbitMQ.");
            return;
        }

        try {
            User user = userRepository.findById(dto.getUserId()).orElse(null);
            if (user == null) {
                logger.warn("User not found for prediction result notification: userId={}", dto.getUserId());
                return;
            }

            // Site İçi Bildirim Oluşturma
            String siteMessage;
            NotificationType type;

            if (dto.getPointsWon() > 0) {
                siteMessage = String.format("Tebrikler! %s vs %s maçı (Gerçek Skor: %d-%d) için yaptığınız tahmin başarılı oldu. %d puan kazandınız!",
                        dto.getHomeTeam(), dto.getAwayTeam(), dto.getActualHomeScore(), dto.getActualAwayScore(), dto.getPointsWon());
                type = NotificationType.PREDICTION_RESULT_WIN;
            } else if (dto.getPointsWon() < 0) {
                siteMessage = String.format("Maalesef, %s vs %s maçı (Gerçek Skor: %d-%d) için yaptığınız tahmin hatalıydı. %d puan kaybettiniz.",
                        dto.getHomeTeam(), dto.getAwayTeam(), dto.getActualHomeScore(), dto.getActualAwayScore(), Math.abs(dto.getPointsWon()));
                type = NotificationType.PREDICTION_RESULT_LOSS;
            } else {
                siteMessage = String.format("%s vs %s maçı (Gerçek Skor: %d-%d) için yaptığınız tahmin işlendi. Yatırdığınız %d puan iade edildi.",
                        dto.getHomeTeam(), dto.getAwayTeam(), dto.getActualHomeScore(), dto.getActualAwayScore(), dto.getPointsStake());
                type = NotificationType.PREDICTION_RESULT_DRAW;
            }
            String siteLink = String.format("%s/#/predictions?highlightMatchId=%d", frontendUrl, dto.getMatchId());
            Notification notification = new Notification(user, siteMessage, type, siteLink, dto.getMatchId());
            notificationRepository.save(notification);
            logger.info("Prediction result site notification created for userId={}, matchId={}", user.getId(), dto.getMatchId());

            // E-posta Bildirimi Gönderme
            // Kullanıcının e-posta bildirim tercihi (örn: user.isPredictionResultEmailEnabled()) kontrol edilebilir.
            // Şimdilik, e-postası varsa ve doğrulanmışsa gönderelim.
            if (user.getEmail() != null && !user.getEmail().isEmpty() && user.isEmailVerified()) {
                logger.info("Preparing to send prediction result email to user: {}", user.getUsername());

                String emailSubject = String.format("Tahmin Sonucunuz: %s vs %s", dto.getHomeTeam(), dto.getAwayTeam());
                String emailResultDetailsHtml = String.format( // Site içi bildirim mesajıyla aynı veya benzer olabilir
                        "<strong>%s vs %s</strong> maçı için yaptığınız tahmin sonuçlandı.<br>" +
                                "Sizin Tahmininiz: %d-%d (%d Puan)<br>" +
                                "Gerçek Skor: %d-%d<br>" +
                                "Sonuç: <strong>%s</strong>",
                        dto.getHomeTeam(), dto.getAwayTeam(),
                        dto.getPredictedHomeScore(), dto.getPredictedAwayScore(), dto.getPointsStake(),
                        dto.getActualHomeScore(), dto.getActualAwayScore(),
                        dto.getPointsWon() > 0 ? dto.getPointsWon() + " puan kazandınız!" : (dto.getPointsWon() < 0 ? Math.abs(dto.getPointsWon()) + " puan kaybettiniz." : "Puanınız değişmedi.")
                );
                String emailPredictionsLink = String.format("%s/#/predictions", frontendUrl);

                emailService.sendPredictionResultEmail(user.getEmail(), user.getUsername(), emailSubject, emailResultDetailsHtml, emailPredictionsLink);
                logger.info("Prediction result email sending process initiated for user: {}", user.getUsername());
            } else {
                logger.info("Skipping prediction result email for user {} (no email, not verified, or disabled).", user.getUsername());
            }

        } catch (Exception e) {
            logger.error("Error processing prediction result notification for userId {}, matchId {}: {}",
                    dto.getUserId(), dto.getMatchId(), e.getMessage(), e);
        }
    }
}