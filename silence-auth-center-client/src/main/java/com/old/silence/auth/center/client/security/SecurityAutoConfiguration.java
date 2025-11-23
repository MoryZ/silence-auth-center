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


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector handlerMappingIntrospector) throws Exception {
        http
                // 禁用 CSRF（适用于无状态 API，如 JWT 认证）
                .csrf(AbstractHttpConfigurer::disable)
                // 配置 CORS
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*")); // 生产环境需指定具体域名
                    corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowCredentials(true);
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
                    if (enable) {
                        // 开启白名单：白名单路径放行，其余需认证
                        if (whiteListApi != null && whiteListApi.length > 0) {
                            auth.requestMatchers(whiteListApi).permitAll();
                        }
                    }
                    // 无论是否开启白名单，所有请求默认需要认证（白名单仅在 enable 为 true 时生效）
                    auth.anyRequest().authenticated();
                })
                // 始终添加 Token 过滤器（因为认证始终启用）
                .addFilterBefore(tokenFilter(), UsernamePasswordAuthenticationFilter.class);
        // 使用自定义的认证入口点，区分接口不存在（404）和认证失败（401）
        http.exceptionHandling(e -> e.authenticationEntryPoint(new NotFoundAwareAuthenticationEntryPoint(handlerMappingIntrospector)));
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