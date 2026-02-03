package com.old.silence.auth.center.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.old.silence.json.JacksonMapper;
import com.old.silence.auth.center.security.exception.TokenVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SilenceAuthCenterServerTokenAuthority implements SilenceAuthCenterTokenAuthority {

    private static final Logger LOGGER = LoggerFactory.getLogger(SilenceAuthCenterServerTokenAuthority.class);

    @Value("${silence.auth.center.jwt.secret:silence-auth-center}")
    private String jwtSecret;

    @Value("${silence.auth.center.jwt.expiration:6}")
    private Long jwtExpirationSeconds;

    public String issueToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("alg", algorithm.getName());
        headerClaims.put("typ", SecurityConstants.TOKEN_TYPE);

        Instant now = Instant.now();
        return JWT.create()
                .withHeader(headerClaims)
                .withSubject(username)
                .withIssuer(SecurityConstants.TOKEN_ISSUER)
                .withAudience(SecurityConstants.TOKEN_AUDIENCE)
                .withIssuedAt(now)
                .withExpiresAt(now.plusSeconds(jwtExpirationSeconds))
                .sign(algorithm);
    }

    @Override
    public String issueToken(SilencePrincipal principal) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("alg", algorithm.getName());
        headerClaims.put("typ", SecurityConstants.TOKEN_TYPE);

        Instant now = Instant.now();
        return JWT.create()
                .withHeader(headerClaims)
                .withSubject(JacksonMapper.getSharedInstance().toJson(principal))
                .withIssuer(SecurityConstants.TOKEN_ISSUER)
                .withAudience(SecurityConstants.TOKEN_AUDIENCE)
                .withIssuedAt(now)
                .withExpiresAt(now.plus(jwtExpirationSeconds, ChronoUnit.HOURS))
                .sign(algorithm);
    }

    @Override
    public boolean verifyToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            verifier.verify(token);
            return true;
        } catch (JWTDecodeException | SignatureVerificationException ex) {
            LOGGER.error("verify token failed:{}", ex.getLocalizedMessage());
            throw new TokenVerificationException(401, "token is invalid", ex);
        } catch (TokenExpiredException ex) {
            LOGGER.warn("The token is expired:{}", token);
            throw new TokenVerificationException(403, "token is expired", ex);
        }
    }

    @Override
    public String getSubject(String token) {
        return JWT.require(Algorithm.HMAC256(jwtSecret))
                .build().verify(token)
                .getSubject();
    }
}
