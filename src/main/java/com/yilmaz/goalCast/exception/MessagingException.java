// com/yilmaz/goalCast/exception/MessagingException.java
package com.yilmaz.goalCast.exception;

public class MessagingException extends RuntimeException {
    public MessagingException(String message) {
        super(message);
    }

    public MessagingException(String message, Throwable cause) {
        super(message, cause);
    }
}