package com.old.silence.auth.center.api.assembler;

import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;
import com.old.silence.auth.center.api.config.AuthCenterMapStructSpringConfig;
import com.old.silence.auth.center.domain.model.Notice;
import com.old.silence.auth.center.dto.NoticeCommand;

/**
 * @author moryzang
 */
@Mapper(uses = AuthCenterMapStructSpringConfig.class)
public interface NoticeMapper extends Converter<NoticeCommand, Notice> {

}
