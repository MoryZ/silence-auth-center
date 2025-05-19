package com.old.silence.auth.center.api.assembler;

import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;
import com.old.silence.auth.center.api.config.AuthCenterMapStructSpringConfig;
import com.old.silence.auth.center.domain.model.User;
import com.old.silence.auth.center.dto.UserCommand;
import com.old.silence.auth.center.vo.UserVo;

/**
 * @author moryzang
 */

@Mapper(uses = AuthCenterMapStructSpringConfig.class)
public interface UserMapper extends Converter<UserCommand, User> {

    User convert(@NotNull UserCommand userCommand);

    UserVo toUserVo(User user);

}