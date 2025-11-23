package com.old.silence.auth.center.api.assembler;

import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;
import com.old.silence.auth.center.domain.model.User;
import com.old.silence.auth.center.dto.UserCommand;
import com.old.silence.auth.center.vo.UserVo;
import com.old.silence.core.mapstruct.MapStructSpringConfig;

/**
 * @author moryzang
 */

@Mapper(uses = MapStructSpringConfig.class)
public interface UserMapper extends Converter<UserCommand, User> {

    User convert(UserCommand userCommand);

    UserVo toUserVo(User user);

}