package com.old.silence.auth.center.client.security;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import com.old.silence.auth.center.security.TokenAuthority;
import com.old.silence.core.condition.ConditionOnPropertyPrefix;

@AutoConfiguration
@ConditionOnPropertyPrefix("silence.auth.center.security.api")
public class SecurityAutoConfiguration {

    @Value("${silence.auth.center.security.api.white-list:}")
    private String[] whiteListApi;

    @Value("${silence.auth.center.security.api.enable:true}")
    private boolean enable;

    @Value("${silence.auth.center.security.api.cors.allowed-origins:}")
    private String[] allowedOrigins;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector handlerMappingIntrospector) throws Exception {
        http
                // 禁用 CSRF（适用于无状态 API，如 JWT 认证）
                .csrf(AbstractHttpConfigurer::disable)
                // 配置 CORS
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    if (allowedOrigins != null && allowedOrigins.length > 0) {
                        corsConfiguration.setAllowedOrigins(Arrays.asList(allowedOrigins));
                        corsConfiguration.setAllowCredentials(true);
                    } else {
                        corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
                        corsConfiguration.setAllowCredentials(false);
                    }
                    corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(Collections.singletonList("*")); // 按需细化
                    corsConfiguration.setExposedHeaders(Collections.singletonList("*")); // 按需细化
                    corsConfiguration.setMaxAge(3600L); // 预检请求缓存时间
                    return corsConfiguration;
                }))
                // 无状态会话管理（适合令牌认证）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 配置请求授权规则（核心：认证始终启用，仅白名单根据 enable 切换）
                .authorizeHttpRequests(auth -> {
                    if (!enable) {
                        auth.anyRequest().permitAll();
                        return;
                    }
                    // 开启白名单：白名单路径放行，其余需认证
                    if (whiteListApi != null && whiteListApi.length > 0) {
                        auth.requestMatchers(whiteListApi).permitAll();
                    }
                    auth.anyRequest().authenticated();
                });
        if (enable) {
            // 仅在启用时添加 Token 过滤器
            http.addFilterBefore(tokenFilter(), UsernamePasswordAuthenticationFilter.class);
        }
        // 配置异常处理：只处理认证/授权异常，其他异常继续传播到 Spring MVC 的异常处理器
        http.exceptionHandling(e -> {
            // 使用自定义的认证入口点，区分接口不存在（404）和认证失败（401）
            e.authenticationEntryPoint(new NotFoundAwareAuthenticationEntryPoint(handlerMappingIntrospector));
            // 注意：这里不配置 accessDeniedHandler，让 AccessDeniedException 也继续传播
            // 这样业务异常（如 SQL 异常）不会被 Security 拦截，而是由 Spring MVC 的全局异常处理器处理
        });
        return http.build();
    }


    private TokenFilter tokenFilter() {
        var tokenAuthority = new TokenAuthority();
        return new TokenFilter(tokenAuthority);
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

} 