package com.old.silence.auth.center.client.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
        http = http.cors().and().csrf().disable();
        http = http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();

        if(enable){
            if (whiteListApi.length > 0){
                http.authorizeRequests()
                        .antMatchers(whiteListApi).permitAll()
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