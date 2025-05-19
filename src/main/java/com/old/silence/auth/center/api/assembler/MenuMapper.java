package com.old.silence.auth.center.api.assembler;

import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;
import com.old.silence.auth.center.api.config.AuthCenterMapStructSpringConfig;
import com.old.silence.auth.center.domain.model.Menu;
import com.old.silence.auth.center.dto.MenuCommand;
import com.old.silence.auth.center.dto.MenuDto;


/**
 * @author moryzang
 */

@Mapper(uses = AuthCenterMapStructSpringConfig.class)
public interface MenuMapper extends Converter<MenuCommand, Menu> {

    Menu convert(@NotNull MenuCommand menuCommand);

    MenuDto convertToDto(Menu menu);

}