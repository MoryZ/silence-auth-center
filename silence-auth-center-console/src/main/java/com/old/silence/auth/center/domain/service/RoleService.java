package com.old.silence.auth.center.domain.service;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.auth.center.api.assembler.RoleMapper;
import com.old.silence.auth.center.domain.model.Role;
import com.old.silence.auth.center.domain.model.RoleMenu;
import com.old.silence.auth.center.domain.repository.RoleMenuRepository;
import com.old.silence.auth.center.domain.repository.RoleRepository;
import com.old.silence.auth.center.infrastructure.message.AuthCenterMessages;
import com.old.silence.auth.center.vo.RoleVo;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMenuRepository roleMenuRepository;

    public RoleService(RoleRepository roleRepository, RoleMenuRepository roleMenuRepository) {
        this.roleRepository = roleRepository;
        this.roleMenuRepository = roleMenuRepository;
    }


    public Page<Role> query(Page<Role> page, QueryWrapper<Role> queryWrapper) {
        // 构建查询条件
        queryWrapper.orderByDesc( "created_date");

        // 执行分页查询
        return roleRepository.query(page, queryWrapper);
    }

    
    public List<RoleVo> listAllRoles() {
        List<Role> roles = roleRepository.findByStatusAndDeleted(true, false);

        return roles.stream()
                .map(this::enhanceRole)
                .collect(Collectors.toList());
    }

    
    public Role findById(BigInteger id) {
        return getRole(id);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public BigInteger create(Role role) {
        // 检查角色编码是否已存在
        if (isRoleCodeExists(role.getCode())) {
            throw AuthCenterMessages.ROLE_ALREADY_EXIST.createException("角色编码已存在");
        }
        roleRepository.create(role);

        return role.getId();
    }

    
    @Transactional(rollbackFor = Exception.class)
    public void update(Role role) {
        // 检查角色是否存在
        var existingRole = getRole(role.getId());

        // 检查角色编码是否已被其他角色使用
        if (!existingRole.getCode().equals(role.getCode())
                && isRoleCodeExists(role.getCode())) {
            throw AuthCenterMessages.ROLE_ALREADY_EXIST.createException("角色编码已存在");
        }

        roleRepository.update(role);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public void delete(BigInteger id) {
        // 检查角色是否存在
        var role = getRole(id);

        // 逻辑删除角色
        role.setDeleted(true);
        roleRepository.delete(id);

    }

    
    private Role getRole(BigInteger id) {
        // 检查角色是否存在
        Role role = roleRepository.findById(id);
        if (role == null || role.getDeleted()) {
            throw AuthCenterMessages.ROLE_NOT_EXIST.createException("角色不存在");
        }
        return role;
    }

    public void updateStatus(BigInteger id, Boolean status) {
        // 检查角色是否存在
        var role = getRole(id);

        // 更新状态
        role.setStatus(status);
        roleRepository.update(role);

    }

    
    public List<BigInteger> getRoleMenuIds(BigInteger roleId) {
        return roleMenuRepository.findByRoleId(roleId).stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
    }


    private RoleVo enhanceRole(Role role) {
        var roleMapper = Mappers.getMapper(RoleMapper.class);
        var roleVo = roleMapper.convertToDto(role);
        roleVo.setMenuIds(getRoleMenuIds(role.getId()));
        return roleVo;
    }

    private boolean isRoleCodeExists(String code) {
        return roleRepository.existsByCodeAndDeleted(code, false);
    }

    public void assignRoleMenus(BigInteger id, List<BigInteger> menuIds) {
        var role = roleRepository.findById(id);
        if (role == null || role.getDeleted()) {
            throw AuthCenterMessages.ROLE_NOT_EXIST.createException("角色不存在");
        }

        // 清空角色菜单关联
        roleMenuRepository.deleteByRoleId(role.getId());


        // 重新分配角色菜单关联
        var roleMenus = menuIds.stream().map(menuId -> {
            return new RoleMenu(id, menuId);
        }).collect(Collectors.toList());

        roleMenuRepository.bulkInsert(roleMenus);
    }

   
}