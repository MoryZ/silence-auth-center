package com.old.silence.auth.center.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.old.silence.auth.center.security.exception.TokenVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenAuthority implements SilenceAuthCenterTokenAuthority {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenAuthority.class);
    private static final int STATUS_UNAUTHORIZED = 401;
    private static final int STATUS_FORBIDDEN = 403;

    @Override
    public String issueToken(SilencePrincipal principal) {
        Algorithm algorithm = Algorithm.HMAC256(SecurityConstants.getTokenSecret());
        return JWT.create()
                .withSubject(principal.getUsername())
                .withIssuer(SecurityConstants.TOKEN_ISSUER)
                .withAudience(SecurityConstants.TOKEN_AUDIENCE)
                .sign(algorithm);
    }

    @Override
    public boolean verifyToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SecurityConstants.getTokenSecret());
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            verifier.verify(token);
            return true;
        } catch (JWTDecodeException | SignatureVerificationException ex) {
            LOGGER.error("Token verification failed: {}", ex.getLocalizedMessage());
            throw new TokenVerificationException(STATUS_UNAUTHORIZED, "token is invalid", ex);
        } catch (TokenExpiredException ex) {
            LOGGER.debug("Token expired: {}", ex.getLocalizedMessage());
            throw new TokenVerificationException(STATUS_FORBIDDEN, "token is expired", ex);
        }
    }

    @Override
    public String getSubject(String token) {
        return JWT.require(Algorithm.HMAC256(SecurityConstants.getTokenSecret()))
                .build()
                .verify(token)
                .getSubject();
    }
}
