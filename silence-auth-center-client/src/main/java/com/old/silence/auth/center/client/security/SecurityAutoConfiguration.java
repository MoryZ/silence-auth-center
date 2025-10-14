package com.old.silence.auth.center.client.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import com.old.silence.auth.center.security.TokenAuthority;
import com.old.silence.core.condition.ConditionOnPropertyPrefix;

@AutoConfiguration
@ConditionOnPropertyPrefix("silence.auth.center.security.api")
public class SecurityAutoConfiguration  {

    @Value("${silence.auth.center.security.api.white-list:}")
    private String[] whiteListApi;

    @Value("${silence.auth.center.security.api.enable:true}")
    private boolean enable;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http = http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.addAllowedOriginPattern("*");
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS));

        if(enable){
            if (whiteListApi.length > 0){
                http.authorizeRequests()
                        .requestMatchers(whiteListApi).permitAll()
                        .anyRequest().authenticated();
            }else {
                http.authorizeRequests().anyRequest().authenticated();
            }
            http.addFilterBefore(tokenFilter(), UsernamePasswordAuthenticationFilter.class);
        }else {
            http.authorizeRequests().anyRequest().permitAll();
        }

        http.addFilterAt(tokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        return http.build();
    }

    private TokenFilter tokenFilter(){
        var tokenAuthority = new TokenAuthority();
        return new TokenFilter(tokenAuthority);
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

} 