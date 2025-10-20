package com.old.silence.auth.center.infrastructure.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.old.silence.auth.center.security.SilenceAuthCenterContextHolder;
import com.old.silence.core.security.UserContextAware;
import com.old.silence.webmvc.data.UserHeaderAuditorAware;

/**
 * @author moryzang
 */
@Configuration(proxyBeanMethods = false)
public class AuditorAwareConfiguration {

    @Bean
    public UserContextAware<String> userContextAware() {
        return ()-> Optional.of(getCurrentAuditor());
    }

    public String getCurrentAuditor() {
       return SilenceAuthCenterContextHolder.getAuthenticatedUserName().orElse("SYSTEM");
    }

}
