package com.old.silence.auth.center.domain.service;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.auth.center.enums.MenuType;
import com.old.silence.core.util.CollectionUtils;
import com.old.silence.core.util.TreeFormatUtils;
import com.old.silence.dto.TreeDto;
import com.old.silence.auth.center.api.assembler.MenuMapper;
import com.old.silence.auth.center.domain.model.Menu;
import com.old.silence.auth.center.domain.model.RoleMenu;
import com.old.silence.auth.center.domain.model.UserRole;
import com.old.silence.auth.center.domain.repository.MenuRepository;
import com.old.silence.auth.center.domain.repository.RoleMenuRepository;
import com.old.silence.auth.center.domain.repository.UserRoleRepository;
import com.old.silence.auth.center.dto.MenuDto;
import com.old.silence.auth.center.infrastructure.message.AuthCenterMessages;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final UserRoleRepository userRoleRepository;

    public MenuService(MenuRepository menuRepository, RoleMenuRepository roleMenuRepository,
                       UserRoleRepository userRoleRepository) {
        this.menuRepository = menuRepository;
        this.roleMenuRepository = roleMenuRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public Page<Menu> query(Page<Menu> page, QueryWrapper<Menu> queryWrapper) {
        return menuRepository.query(page, queryWrapper);
    }

    public List<TreeDto> getMenuTree() {
        // 获取所有菜单列表
        List<Menu> menus = menuRepository.findAllByDeleted(false);
        var treeDTOS = CollectionUtils.transformToList(menus, node -> new TreeDto(node.getId(), node.getName(), node.getParentId()));


        // 转换为树形结构
        return TreeFormatUtils.listToTree(treeDTOS, TreeDto::getId, TreeDto::getParentId, TreeDto::setChildren);
    }

    public List<MenuDto> getMenuList() {
        // 获取所有菜单列表
        var menus = menuRepository.findAllByDeleted(false);
        // 转换为树形结构
        return buildMenuTree(menus);
    }

    public Menu findById(BigInteger id) {
        Menu menu = menuRepository.findById(id);
        if (menu == null || menu.getDeleted() ) {
            throw AuthCenterMessages.MENU_NOT_EXIST.createException("菜单不存在");
        }
        return menu;
    }

    
    @Transactional(rollbackFor = Exception.class)
    public BigInteger create(Menu menu) {
        // 创建菜单
        menu.setDeleted(false);
        menuRepository.create(menu);
        return menu.getId();
    }

    
    @Transactional(rollbackFor = Exception.class)
    public void update(Menu menu) {
        // 检查菜单是否存在
        Menu existingMenu = menuRepository.findById(menu.getId());
        if (existingMenu == null || existingMenu.getDeleted() ) {
            throw AuthCenterMessages.MENU_NOT_EXIST.createException("菜单不存在");
        }

        // 更新菜单信息
        menuRepository.update(menu);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public void delete(BigInteger id) {

        menuRepository.delete(id);
        // 检查菜单是否存在
        Menu menu = menuRepository.findById(id);
        if (menu == null || menu.getDeleted() ) {
            throw AuthCenterMessages.MENU_NOT_EXIST.createException();
        }

        // 检查是否有子菜单
        boolean hasChildren = menuRepository.existsByParentIdAndDeleted(id, false);
        if (hasChildren) {
            throw AuthCenterMessages.SUB_MENU_EXIST.createException();
        }

        // 逻辑删除菜单
        menu.setDeleted(true);
        menuRepository.delete(id);

    }

    public void updateMenuStatus(BigInteger id, Boolean status) {
        // 检查菜单是否存在
        Menu menu = menuRepository.findById(id);
        if (menu == null || menu.getDeleted() ) {
            throw AuthCenterMessages.MENU_NOT_EXIST.createException();
        }

        // 更新状态
        menu.setStatus(status);
        menuRepository.update(menu);
    }

    
    public List<MenuDto> getCurrentUserMenuTree(BigInteger userId) {
        var menus =  getCurrentUserMenus(userId);
        return buildMenuTree(menus);
    }

    
    public List<Menu> getCurrentUserMenus(BigInteger userId) {

        List<Menu> menus;
        //如果是超管，返回所有资源
        if (BigInteger.ONE.compareTo(userId) == 0) {
            menus = menuRepository.findAllByDeleted(false)
                    .stream().filter(menu -> Set.of(MenuType.CONTENTS, MenuType.MENU).contains(menu.getType()) && menu.getStatus())
                    .collect(Collectors.toList());
        } else {
            // 获取用户角色ID列表
            List<BigInteger> roleIds = userRoleRepository.findByUserId(userId)
                    .stream()
                    .map(UserRole::getRoleId)
                    .collect(Collectors.toList());

            // 获取角色菜单ID列表
            List<BigInteger> menuIds = roleMenuRepository.findByRoleIdIn(roleIds)
                    .stream()
                    .map(RoleMenu::getMenuId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(menuIds)) {
                return List.of();
            }

            // 获取权限标识列表
            menus = menuRepository.findByIdInAndDeletedAndTypeInAndStatus(menuIds, false, List.of(MenuType.CONTENTS, MenuType.MENU), true);

        }
        return menus;


    }


    private List<MenuDto> buildMenuTree(List<Menu> menus) {
        var menuMapper = Mappers.getMapper(MenuMapper.class);
        var menuDtos = menus.stream().map(menuMapper::convertToDto).collect(Collectors.toList());
        // 构建父子关系
        Map<BigInteger, List<MenuDto>> parentMap = menuDtos.stream()
                .collect(Collectors.groupingBy(MenuDto::getParentId));

        // 设置子菜单
        menuDtos.forEach(menu -> menu.setChildren(parentMap.getOrDefault(menu.getId(), new ArrayList<>())));

        // 返回顶层菜单
        return parentMap.getOrDefault(BigInteger.ZERO, new ArrayList<>());
    }



}