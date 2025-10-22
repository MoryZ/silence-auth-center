package com.old.silence.auth.center.client.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import com.old.silence.auth.center.security.SecurityConstants;
import com.old.silence.auth.center.security.SilenceAuthCenterGrantedAuthority;
import com.old.silence.auth.center.security.SilenceAuthCenterTokenAuthority;
import com.old.silence.auth.center.security.SilencePrincipal;
import com.old.silence.json.JacksonMapper;

public class TokenFilter extends OncePerRequestFilter {

    private final SilenceAuthCenterTokenAuthority tokenAuthority;

    public TokenFilter(SilenceAuthCenterTokenAuthority tokenAuthority) {
        this.tokenAuthority = tokenAuthority;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        var token = getToken(request);
        if (StringUtils.hasText(token) && tokenAuthority.verifyToken(token)) {
            String subject = tokenAuthority.getSubject(token);
            var jacksonMapper = JacksonMapper.getSharedInstance();
            if (jacksonMapper.validateJson(subject)) {
                var principal = jacksonMapper.fromJson(subject, SilencePrincipal.class);
                var authorities = principal.getRoles()
                        .stream()
                        .map(roleDto -> new SilenceAuthCenterGrantedAuthority(roleDto.getRoleCode(), roleDto.getRoleName(), roleDto.getAppCode()))
                        .collect(Collectors.toCollection(() -> new ArrayList<GrantedAuthority>()));
                Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(request, wrapper);
        wrapper.copyBodyToResponse();
    }

    private String getToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            return null;
        }
        return authorizationHeader.replace(SecurityConstants.TOKEN_PREFIX, "");
    }
}
