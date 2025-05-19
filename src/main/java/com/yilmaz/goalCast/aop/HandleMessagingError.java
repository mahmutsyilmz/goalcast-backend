package com.yilmaz.goalCast.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HandleMessagingError {
    String operation() default "messaging operation";
    boolean rethrowException() default true; // YENİ: Hata tekrar fırlatılsın mı?
}