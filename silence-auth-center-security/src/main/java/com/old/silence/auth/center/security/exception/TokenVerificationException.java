package com.old.silence.auth.center.security.exception;

/**
 * Token verification exception to carry http status code for downstream handlers.
 */
public class TokenVerificationException extends RuntimeException {

    private final int statusCode;

    public TokenVerificationException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}


