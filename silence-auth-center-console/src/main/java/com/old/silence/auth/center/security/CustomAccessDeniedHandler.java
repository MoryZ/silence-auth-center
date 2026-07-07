package com.old.silence.auth.center.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import com.old.silence.json.JacksonMapper;
import com.old.silence.webmvc.common.ApiResult;

/**
 * @author moryzang
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final JacksonMapper jacksonMapper;

    public CustomAccessDeniedHandler(JacksonMapper jacksonMapper) {
        this.jacksonMapper = jacksonMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 使用你的统一返回对象
        ApiResult<?> result = ApiResult.error(HttpStatus.FORBIDDEN.value(), "权限不足：您没有权限访问该资源");

        response.getWriter().write(jacksonMapper.toJson(result));
    }
}
