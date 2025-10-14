package com.old.silence.auth.center.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.old.silence.auth.center.domain.service.UserService;
import com.old.silence.core.util.CollectionUtils;
import com.old.silence.json.JacksonMapper;


import java.io.IOException;
import java.util.Optional;

/**
 * @author moryzang
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final SilenceAuthCenterServerTokenAuthority jwtProvider;
    private final UserService userService;

    private final JacksonMapper jacksonMapper;

    public JwtFilter(SilenceAuthCenterServerTokenAuthority jwtProvider,
                     UserService userService,
                     JacksonMapper jacksonMapper) {
        this.jwtProvider = jwtProvider;
        this.userService = userService;
        this.jacksonMapper = jacksonMapper;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws
            ServletException, IOException {
        var tokenOptional = getToken(request);
        if ( tokenOptional.isPresent() && jwtProvider.verifyToken(tokenOptional.get())) {
            var token = tokenOptional.get();
            String subject = jwtProvider.getSubject(token);
            if (jacksonMapper.validateJson(subject)){
                var principal = jacksonMapper.fromJson(subject, SilencePrincipal.class);
                var username = principal.getUsername();

                if(userService.existsByUsername(username)) {
                    var authorities = CollectionUtils.transformToList(
                            principal.getRoles(), silenceAuthCenterRole -> new SilenceAuthCenterGrantedAuthority(silenceAuthCenterRole.getRoleCode(), silenceAuthCenterRole.getRoleName(),
                                    silenceAuthCenterRole.getAppCode()));

                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            principal, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> getToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authorizationHeader == null || !authorizationHeader.startsWith(SecurityConstants.TOKEN_PREFIX)){
            return Optional.empty();
        }
        return Optional.of(authorizationHeader.replace(SecurityConstants.TOKEN_PREFIX, ""));

    }
}