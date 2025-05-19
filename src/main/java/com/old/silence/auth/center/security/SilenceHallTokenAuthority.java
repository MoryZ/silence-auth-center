package com.old.silence.auth.center.security;

public interface SilenceHallTokenAuthority {

    String issueToken(SilencePrincipal principal);

    boolean verifyToken(String token);

    String getSubject(String token);
}
