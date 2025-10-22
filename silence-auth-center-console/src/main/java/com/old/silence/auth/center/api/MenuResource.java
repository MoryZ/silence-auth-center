package com.old.silence.auth.center.api;

import java.math.BigInteger;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.auth.center.api.assembler.MenuMapper;
import com.old.silence.auth.center.domain.model.Menu;
import com.old.silence.auth.center.domain.service.MenuService;
import com.old.silence.auth.center.dto.MenuCommand;
import com.old.silence.auth.center.dto.MenuDto;
import com.old.silence.auth.center.dto.MenuQuery;
import com.old.silence.data.commons.converter.QueryWrapperConverter;
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
    @PreAuthorize("@perm.hasAuthority('system:menu:tree')")
    public List<TreeDto> getSysMenuTree() {
        return menuService.getMenuTree();
    }


    @GetMapping(path = "/menus/list")
    @PreAuthorize("@perm.hasAuthority('system:menu:list')")
    public List<MenuDto> getSysMenuList() {
        return menuService.getMenuList();

    }

    @GetMapping(path = "/menus", params = {"pageNo", "pageSize"})
    @PreAuthorize("@perm.hasAuthority('system:menu:page')")
    public Page<Menu> query(Page<Menu> page, MenuQuery menuQuery) {
        var queryWrapper = QueryWrapperConverter.convert(menuQuery, Menu.class);
        return menuService.query(page, queryWrapper);
    }

    @GetMapping("/menus/{id}")
    @PreAuthorize("@perm.hasAuthority('system:menu:detail')")
    public Menu findById(@PathVariable BigInteger id) {
        return menuService.findById(id);
    }

    @PostMapping("/menus")
    @PreAuthorize("@perm.hasAuthority('system:menu:add')")
    public BigInteger create(@RequestBody @Validated MenuCommand menuCommand) {
        var menu = menuMapper.convert(menuCommand);
        return menuService.create(menu);
    }

    @PutMapping("/menus/{id}")
    @PreAuthorize("@perm.hasAuthority('system:menu:edit')")
    public void update(@PathVariable BigInteger id, @RequestBody @Validated MenuCommand menuCommand) {
        var menu = menuMapper.convert(menuCommand);
        menu.setId(id); //NO SONAR
        menuService.update(menu);
    }

    @PutMapping("/menus/{id}/enable")
    @PreAuthorize("@perm.hasAuthority('system:menu:enable')")
    public void enable(@PathVariable BigInteger id) {
        menuService.updateMenuStatus(id, true);
    }

    @PutMapping("/menus/{id}/disable")
    @PreAuthorize("@perm.hasAuthority('system:menu:disable')")
    public void disable(@PathVariable BigInteger id) {
        menuService.updateMenuStatus(id, false);
    }

    @DeleteMapping("/menus/{id}")
    @PreAuthorize("@perm.hasAuthority('system:menu:delete')")
    public void delete(@PathVariable BigInteger id) {
        menuService.delete(id);
    }


} 