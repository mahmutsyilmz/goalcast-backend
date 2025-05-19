package com.yilmaz.goalCast.aop;

import com.yilmaz.goalCast.exception.MessagingException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class MessagingErrorHandlingAspect {

    private static final Logger logger = LoggerFactory.getLogger(MessagingErrorHandlingAspect.class);

    @Around("@annotation(handleMessagingErrorAnnotation)") // Anotasyon parametresini almak için
    public Object handleMessagingError(ProceedingJoinPoint joinPoint, HandleMessagingError handleMessagingErrorAnnotation) throws Throwable {
        String operationDescription = handleMessagingErrorAnnotation.operation();
        boolean rethrow = handleMessagingErrorAnnotation.rethrowException();

        try {
            return joinPoint.proceed();
        } catch (AmqpException amqpEx) {
            logger.error("AOP: AMQPException during [{}]: {}", operationDescription, amqpEx.getMessage(), amqpEx);
            if (rethrow) {
                throw new MessagingException("Failed to complete " + operationDescription + ". RabbitMQ communication error.", amqpEx);
            }
            // Eğer rethrow false ise, metot void ise null döner, değilse uygun bir varsayılan değer gerekebilir
            // (veya çağıran tarafın null kontrolü yapması gerekir).
            // Şimdilik, eğer void değilse ve rethrow false ise null dönecek, bu durumu çağıran tarafın bilmesi gerekir.
            return getReturnTypeDefaultValue(joinPoint);
        } catch (Exception ex) {
            logger.error("AOP: Unexpected exception during [{}]: {}", operationDescription, ex.getMessage(), ex);
            if (rethrow) {
                throw new MessagingException("An unexpected error occurred during " + operationDescription + ".", ex);
            }
            return getReturnTypeDefaultValue(joinPoint);
        }
    }

    private Object getReturnTypeDefaultValue(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> returnType = signature.getReturnType();
        if (returnType.equals(Void.TYPE)) {
            return null; // void metotlar için
        }
        // Diğer tipler için varsayılan değerler eklenebilir, ama genellikle null yeterlidir
        // veya bu durumun oluşmaması hedeflenir.
        logger.warn("AOP: Suppressed exception for non-void method returning null. Operation: [{}]",
                ((HandleMessagingError)signature.getMethod().getAnnotation(HandleMessagingError.class)).operation());
        return null;
    }
}