package com.old.silence.auth.center.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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


import java.math.BigInteger;
import java.util.List;

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
    @PreAuthorize("hasAuthority('system:SysMenu:tree')")
    public List<TreeDto> getSysMenuTree() {
        return menuService.getMenuTree();
    }


    @GetMapping(path = "/menus/list")
    @PreAuthorize("hasAuthority('system:SysMenu:tree')")
    public List<MenuDto> getSysMenuList() {
        return menuService.getMenuList();

    }

    @GetMapping(path = "/menus", params = {"pageNo", "pageSize"})
    @PreAuthorize("hasAuthority('system:SysMenu:page')")
    public Page<Menu> query(Page<Menu> page, MenuQuery menuQuery) {
        var queryWrapper = QueryWrapperConverter.convert(menuQuery, Menu.class);
        return menuService.query(page, queryWrapper );
    }

    @GetMapping("/menus/{id}")
    @PreAuthorize("hasAuthority('system:SysMenu:list')")
    public Menu findSysMenuById(@PathVariable BigInteger id) {
        return menuService.findById(id);
    }

    @PostMapping("/menus")
    @PreAuthorize("hasAuthority('system:SysMenu:add')")
    public BigInteger create(@RequestBody MenuCommand menuCommand) {
        var menu = menuMapper.convert(menuCommand);
        return menuService.create(menu);
    }

    @PutMapping("/menus/{id}")
    @PreAuthorize("hasAuthority('system:SysMenu:edit')")
    public void update(@PathVariable BigInteger id, @RequestBody MenuCommand menuCommand) {
        var menu =menuMapper.convert(menuCommand);
        menu.setId(id); //NO SONAR
        menuService.update(menu);
    }

    @DeleteMapping("/menus/{id}")
    @PreAuthorize("hasAuthority('system:SysMenu:delete')")
    public void delete(@PathVariable BigInteger id) {
        menuService.delete(id);
    }

    @PutMapping("/menus/{id}/disable")
    @PreAuthorize("hasAuthority('system:SysMenu:edit')")
    public void updateSysMenuStatus(@PathVariable BigInteger id, @RequestParam Boolean status) {
        menuService.updateMenuStatus(id, status);
    }


} 