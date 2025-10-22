package com.old.silence.auth.center.api.assembler;

import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;
import com.old.silence.auth.center.domain.model.Role;
import com.old.silence.auth.center.dto.RoleCommand;
import com.old.silence.auth.center.vo.RoleVo;
import com.old.silence.core.mapstruct.MapStructSpringConfig;

/**
 * @author moryzang
 */

@Mapper(uses = MapStructSpringConfig.class)
public interface RoleMapper extends Converter<RoleCommand, Role> {

    RoleVo convertToDto(Role role);

}