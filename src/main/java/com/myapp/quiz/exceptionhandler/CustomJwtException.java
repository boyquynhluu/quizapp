package com.myapp.quiz.exceptionhandler;

@SuppressWarnings("serial")
public class CustomJwtException extends RuntimeException {
    public CustomJwtException(String message, Throwable cause) {
        super(message, cause);
    }
}
