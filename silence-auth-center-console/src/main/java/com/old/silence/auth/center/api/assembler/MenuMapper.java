package com.old.silence.auth.center.api.assembler;

import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;
import com.old.silence.auth.center.domain.model.Menu;
import com.old.silence.auth.center.dto.MenuCommand;
import com.old.silence.auth.center.dto.MenuDto;
import com.old.silence.core.mapstruct.MapStructSpringConfig;


/**
 * @author moryzang
 */

@Mapper(uses = MapStructSpringConfig.class)
public interface MenuMapper extends Converter<MenuCommand, Menu> {

    Menu convert(MenuCommand menuCommand);

    MenuDto convertToDto(Menu menu);

}