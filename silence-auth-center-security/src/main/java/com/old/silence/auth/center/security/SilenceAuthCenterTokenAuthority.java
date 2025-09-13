package com.old.silence.auth.center.security;

public interface SilenceAuthCenterTokenAuthority {

    String issueToken(SilencePrincipal principal);

    boolean verifyToken(String token);

    String getSubject(String token);
}
