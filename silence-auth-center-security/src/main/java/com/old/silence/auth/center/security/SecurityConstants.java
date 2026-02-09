package com.old.silence.auth.center.security;

public class SecurityConstants {

    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_AUDIENCE = "silence-auth-center";
    public static final String TOKEN_ISSUER = "silence-auth-center";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_SECRET = "";
    public static final String ROOT_ROLE_CODE = "root";
    public static final String MASTER_ROLE_CODE = "master";

    public static String getTokenSecret() {
        String secret = System.getProperty("SILENCE_AUTH_CENTER_JWT_SECRET");
        if (secret == null || secret.isBlank()) {
            secret = System.getenv("SILENCE_AUTH_CENTER_JWT_SECRET");
        }
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret is not configured. Set SILENCE_AUTH_CENTER_JWT_SECRET.");
        }
        return secret;
    }
}
