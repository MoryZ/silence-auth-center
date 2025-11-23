package com.old.silence.auth.center.client.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.List;

/**
 * @author moryzang
 * 智能认证入口点：区分 401 和 404
 */
@Component
public class SmartAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final List<HandlerMapping> handlerMappings;

    public SmartAuthenticationEntryPoint(List<HandlerMapping> handlerMappings) {
        this.handlerMappings = handlerMappings;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // 检查路径是否存在
        if (!isPathExists(request)) {
            // 路径不存在，返回 404
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.getWriter().write("{\"code\":404,\"message\":\"\"}");
        } else {
            // 路径存在但认证失败，返回 401
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("{\"code\":401,\"message\":\"\"}");
        }
    }

    private boolean isPathExists(HttpServletRequest request) {
        try {
            for (HandlerMapping handlerMapping : handlerMappings) {
                HandlerExecutionChain handler = handlerMapping.getHandler(request);
                if (handler != null) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
