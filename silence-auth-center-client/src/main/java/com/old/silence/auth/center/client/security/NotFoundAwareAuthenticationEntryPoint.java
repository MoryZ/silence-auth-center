package com.old.silence.auth.center.client.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Optional;

/**
 * 自定义认证入口点，用于区分接口不存在（404）和认证失败（401）
 * 
 * 注意：此入口点只会在认证失败时被调用，不会在 Controller 执行过程中被调用。
 * Controller 中抛出的异常（如 SQL 异常）应该由全局异常处理器处理，返回 500。
 * 
 * @author moryzang
 */
public class NotFoundAwareAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerMappingIntrospector handlerMappingIntrospector;

    public NotFoundAwareAuthenticationEntryPoint(HandlerMappingIntrospector handlerMappingIntrospector) {
        this.handlerMappingIntrospector = handlerMappingIntrospector;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) {
        try {
            // 检查请求路径是否匹配任何已注册的处理器
            // 注意：这里只检查处理器映射，不实际执行处理器
            // AuthenticationEntryPoint 只会在认证失败时被调用，此时请求还没有到达 Controller
            Optional<HandlerExecutionChain> handler = handlerMappingIntrospector.getHandlerMappings()
                    .stream()
                    .map(mapping -> {
                        try {
                            return mapping.getHandler(request);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(h -> h != null)
                    .findFirst();

            // 如果找不到匹配的处理器，说明接口不存在，返回 404
            if (handler.isEmpty()) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"status\":404,\"message\":\"\"}");
            } else {
                // 如果找到处理器但认证失败，返回 401
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"status\":401,\"message\":\"\"}");
            }
        } catch (Exception e) {
            // 如果检查过程中出现异常（比如 HandlerMappingIntrospector 未初始化），
            // 默认返回 401，避免影响正常流程
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            try {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"status\":401,\"message\":\"\"}");
            } catch (Exception ex) {
                // 忽略写入异常
            }
        }
    }
}

