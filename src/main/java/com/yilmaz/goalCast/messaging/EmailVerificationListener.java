package com.yilmaz.goalCast.messaging;

import com.yilmaz.goalCast.dto.amqp.EmailVerificationRequestDto;
import com.yilmaz.goalCast.model.User;
import com.yilmaz.goalCast.repository.UserRepository;
import com.yilmaz.goalCast.service.EmailService;
import com.yilmaz.goalCast.config.RabbitMQConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class EmailVerificationListener {

    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationListener.class);

    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.email.verification.token.validity-hours}")
    private int tokenValidityHours;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Autowired
    public EmailVerificationListener(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_VERIFICATION_QUEUE_NAME)
    @Transactional
    public void handleVerificationRequest(EmailVerificationRequestDto requestDto) {
        logger.info("Received email verification request from RabbitMQ: {}", requestDto);

        if (requestDto == null || requestDto.getUserId() == null) {
            logger.error("Received null or invalid request DTO from RabbitMQ.");
            return;
        }

        try {
            User user = userRepository.findById(requestDto.getUserId())
                    .orElse(null);

            if (user == null) {
                logger.error("User not found with ID: {}. Cannot send verification email.", requestDto.getUserId());
                return;
            }

            if (user.isEmailVerified()) {
                logger.info("Email for user {} ({}) is already verified. Skipping email sending.", user.getUsername(), user.getEmail());
                return;
            }

            String token = UUID.randomUUID().toString();
            user.setEmailVerificationToken(token);
            user.setEmailVerificationTokenExpiryDate(LocalDateTime.now().plusHours(tokenValidityHours));

            userRepository.save(user);
            logger.info("Verification token generated and saved for user: {}", user.getUsername());

            String verificationLink = frontendUrl + "/#/verify-email?token=" + token;

            emailService.sendEmailVerificationEmail(
                    user.getEmail(),
                    user.getUsername(),
                    verificationLink
            );

            // E-posta gönderimi @Async olduğu için, bu log e-postanın gerçekten gönderildiği anlamına gelmez,
            // sadece gönderim sürecinin EmailService'e devredildiği anlamına gelir.
            logger.info("Verification email sending process initiated for user: {}", user.getUsername());

        } catch (Exception e) {
            logger.error("Error processing email verification request for userId {}: {}", requestDto.getUserId(), e.getMessage(), e);
        }
    }
}