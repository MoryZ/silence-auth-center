package com.old.silence.auth.center.api.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.extensions.spring.SpringMapperConfig;
import com.old.silence.core.mapstruct.MapStructSpringConfig;

@SpringMapperConfig
@MapperConfig(uses = { MapStructSpringConfig.class, ConversionServiceAdapter.class })
public class AuthCenterMapStructSpringConfig {
}
