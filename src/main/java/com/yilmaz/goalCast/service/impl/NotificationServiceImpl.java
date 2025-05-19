package com.yilmaz.goalCast.service.impl;

import com.yilmaz.goalCast.aop.HandleMessagingError; // IMPORT
import com.yilmaz.goalCast.config.RabbitMQConfig;
import com.yilmaz.goalCast.dto.amqp.EmailVerificationRequestDto;
import com.yilmaz.goalCast.dto.amqp.NewMatchNotificationDto;
import com.yilmaz.goalCast.dto.amqp.PredictionResultNotificationDto;
import com.yilmaz.goalCast.model.User;
import com.yilmaz.goalCast.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public NotificationServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @HandleMessagingError(operation = "queueing email verification request")
    public void sendEmailVerificationRequest(User user) {
        if (user == null) {
            logger.warn("Attempted to send email verification request for a null user.");
            // Burada bir IllegalArgumentException fırlatmak daha uygun olabilir
            // throw new IllegalArgumentException("User cannot be null for sending verification email.");
            return;
        }
        EmailVerificationRequestDto requestDto = new EmailVerificationRequestDto(
                user.getId(), user.getEmail(), user.getUsername());

        logger.info("Sending email verification request to RabbitMQ: {}", requestDto);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_VERIFICATION_EXCHANGE_NAME,
                RabbitMQConfig.EMAIL_VERIFICATION_ROUTING_KEY,
                requestDto);
        logger.info("Email verification request successfully queued for userId={}", user.getId());
    }

    @Override
    @HandleMessagingError(operation = "queueing new match notification")
    public void sendNewMatchAddedNotificationRequest(NewMatchNotificationDto newMatchDto) {
        if (newMatchDto == null || newMatchDto.getMatchId() == null) {
            logger.warn("Attempted to send new match notification request with null or invalid DTO. DTO: {}", newMatchDto);
            return;
        }
        logger.info("Sending new match added notification request to RabbitMQ: matchId={}", newMatchDto.getMatchId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATIONS_TOPIC_EXCHANGE_NAME,
                RabbitMQConfig.NEW_MATCH_ROUTING_KEY,
                newMatchDto);
        logger.info("New match added notification request successfully queued for matchId={}", newMatchDto.getMatchId());
    }

    @Override
    @HandleMessagingError(operation = "queueing prediction result notification")
    public void sendPredictionResultNotificationRequest(PredictionResultNotificationDto predictionResultDto) {
        if (predictionResultDto == null || predictionResultDto.getUserId() == null) {
            logger.warn("Attempted to send prediction result notification request with null or invalid DTO. DTO: {}", predictionResultDto);
            return;
        }
        logger.info("Sending prediction result notification request to RabbitMQ for userId={}, matchId={}",
                predictionResultDto.getUserId(), predictionResultDto.getMatchId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATIONS_TOPIC_EXCHANGE_NAME,
                RabbitMQConfig.PREDICTION_RESULT_ROUTING_KEY,
                predictionResultDto);
        logger.info("Prediction result notification request successfully queued for userId={}, matchId={}",
                predictionResultDto.getUserId(), predictionResultDto.getMatchId());
    }
}