package com.old.silence.auth.center.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.old.silence.json.JacksonMapper;
import com.old.silence.webmvc.common.ApiResult;

/**
 * @author moryzang
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final JacksonMapper jacksonMapper;

    public CustomAuthenticationEntryPoint(JacksonMapper jacksonMapper) {
        this.jacksonMapper = jacksonMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 使用你的统一返回对象
        ApiResult<?> result = ApiResult.error(HttpStatus.UNAUTHORIZED.value(), "认证失败：请先登录或提供有效的Token");

        response.getWriter().write(jacksonMapper.toJson(result));
    }
}