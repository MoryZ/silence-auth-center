package com.old.silence.auth.center.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.old.silence.core.util.CollectionUtils;

@Component
public class PermissionFilter extends OncePerRequestFilter {

    private static final String ACTION_CODE_HEADER = "X-Action-Code";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var currentUserRoles = SilenceAuthCenterContextHolder.getCurrentUserRoles();
        if (CollectionUtils.isEmpty(currentUserRoles)) {
            filterChain.doFilter(request, response);
            return;
        }

        var roleCode = request.getHeader(ACTION_CODE_HEADER);
        if (!CollectionUtils.transformToSet(currentUserRoles, SilenceAuthCenterRole::getRoleCode).contains(roleCode)) {
            writeForbidden(response, "权限不足：您没有权限访问该资源");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeForbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"status\":403,\"message\":\"" + message + "\"}");
    }

}
