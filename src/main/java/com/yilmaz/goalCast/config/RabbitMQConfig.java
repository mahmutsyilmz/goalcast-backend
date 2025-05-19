package com.yilmaz.goalCast.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EMAIL_VERIFICATION_EXCHANGE_NAME = "email.verification.exchange";
    public static final String EMAIL_VERIFICATION_QUEUE_NAME = "email.verification.queue";
    public static final String EMAIL_VERIFICATION_ROUTING_KEY = "email.verify.request";
    public static final String NOTIFICATIONS_TOPIC_EXCHANGE_NAME = "notifications.topic.exchange";
    public static final String NEW_MATCH_NOTIFICATION_QUEUE_NAME = "notifications.newmatch.queue";
    public static final String NEW_MATCH_ROUTING_KEY = "notification.match.new"; // Örnek routing key
    public static final String PREDICTION_RESULT_NOTIFICATION_QUEUE_NAME = "notifications.predictionresult.queue";
    public static final String PREDICTION_RESULT_ROUTING_KEY = "notification.prediction.result"; // Örnek routing key

    @Bean
    TopicExchange notificationsTopicExchange() {
        return new TopicExchange(NOTIFICATIONS_TOPIC_EXCHANGE_NAME);
    }

    // Yeni Maç Kuyruğu
    @Bean
    Queue newMatchNotificationQueue() {
        return new Queue(NEW_MATCH_NOTIFICATION_QUEUE_NAME, true); // durable
    }

    @Bean
    Binding newMatchBinding(Queue newMatchNotificationQueue, TopicExchange notificationsTopicExchange) {
        return BindingBuilder.bind(newMatchNotificationQueue)
                .to(notificationsTopicExchange)
                .with(NEW_MATCH_ROUTING_KEY); // Bu key ile eşleşenler bu kuyruğa gelir
    }

    @Bean
    Queue predictionResultNotificationQueue() {
        return new Queue(PREDICTION_RESULT_NOTIFICATION_QUEUE_NAME, true); // durable
    }

    @Bean
    Binding predictionResultBinding(Queue predictionResultNotificationQueue, TopicExchange notificationsTopicExchange) {
        return BindingBuilder.bind(predictionResultNotificationQueue)
                .to(notificationsTopicExchange)
                .with(PREDICTION_RESULT_ROUTING_KEY);
    }

    @Bean
    DirectExchange emailVerificationExchange() {
        return new DirectExchange(EMAIL_VERIFICATION_EXCHANGE_NAME);
    }

    @Bean
    Queue emailVerificationQueue() {
        return new Queue(EMAIL_VERIFICATION_QUEUE_NAME, true); // durable = true
    }

    @Bean
    Binding emailVerificationBinding(Queue emailVerificationQueue, DirectExchange emailVerificationExchange) {
        return BindingBuilder.bind(emailVerificationQueue)
                .to(emailVerificationExchange)
                .with(EMAIL_VERIFICATION_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
