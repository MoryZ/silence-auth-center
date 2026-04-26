package com.old.silence.auth.center.api;

import java.math.BigInteger;
import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.old.silence.auth.center.api.assembler.MenuMapper;
import com.old.silence.auth.center.domain.model.Menu;
import com.old.silence.auth.center.domain.service.MenuService;
import com.old.silence.auth.center.dto.MenuCommand;
import com.old.silence.auth.center.dto.MenuDto;
import com.old.silence.auth.center.infrastructure.message.AuthCenterMessages;
import com.old.silence.auth.center.security.SilenceAuthCenterContextHolder;
import com.old.silence.dto.TreeDto;

@RestController
@RequestMapping("/api/v1")
public class MenuResource {

    private final MenuMapper menuMapper;
    private final MenuService menuService;

    public MenuResource(MenuMapper menuMapper, MenuService menuService) {
        this.menuMapper = menuMapper;
        this.menuService = menuService;
    }

    @GetMapping(path = "/menus/tree")
    public List<TreeDto> getSysMenuTree() {
        return menuService.getMenuTree();
    }


    @GetMapping(path = "/menus/list")
    public List<MenuDto> getSysMenuList() {
        return menuService.getMenuList();

    }

    @GetMapping(path = "/menus/my")
    public List<MenuDto> getCurrentUserMenus() {
        var userId = SilenceAuthCenterContextHolder.getAuthenticatedUserId()
                .orElseThrow(AuthCenterMessages.USER_NOT_EXIST::createException);
        return menuService.getCurrentUserMenuTree(userId);
    }

    @GetMapping("/menus/{id}")
    public Menu findById(@PathVariable BigInteger id) {
        return menuService.findById(id);
    }

    @PostMapping("/menus")
    public BigInteger create(@RequestBody @Validated MenuCommand menuCommand) {
        var menu = menuMapper.convert(menuCommand);
        return menuService.create(menu);
    }

    @PutMapping("/menus/{id}")
    public void update(@PathVariable BigInteger id, @RequestBody @Validated MenuCommand menuCommand) {
        var menu = menuMapper.convert(menuCommand);
        menu.setId(id); //NO SONAR
        menuService.update(menu);
    }

    @PutMapping("/menus/{id}/enable")
    public void enable(@PathVariable BigInteger id) {
        menuService.updateMenuStatus(id, true);
    }

    @PutMapping("/menus/{id}/disable")
    public void disable(@PathVariable BigInteger id) {
        menuService.updateMenuStatus(id, false);
    }

    @DeleteMapping("/menus/{id}")
    public void delete(@PathVariable BigInteger id) {
        menuService.delete(id);
    }


} 