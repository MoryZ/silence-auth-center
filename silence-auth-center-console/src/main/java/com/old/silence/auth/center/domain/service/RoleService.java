package com.old.silence.auth.center.domain.service;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;
    private final RoleMenuRepository roleMenuRepository;

    public RoleService(RoleRepository roleRepository, RoleMenuRepository roleMenuRepository) {
        this.roleRepository = roleRepository;
        this.roleMenuRepository = roleMenuRepository;
    }

    public Page<Role> queryPage(Page<Role> page, QueryWrapper<Role> queryWrapper) {
        return roleRepository.query(page, queryWrapper);
    }

    public List<RoleVo> minimumRoles() {
        List<Role> roles = roleRepository.findByStatus(true);
        return roles.stream()
                .filter(role -> !"ROLE_ADMIN".equals(role.getCode()))
                .map(this::enhanceRole)
                .collect(Collectors.toList());
    }


    public List<RoleVo> listAllRoles() {
        List<Role> roles = roleRepository.findByStatus(true);

        return roles.stream()
                .map(this::enhanceRole)
                .collect(Collectors.toList());
    }


    public Role findById(BigInteger id) {
        return getRole(id);
    }


    @Transactional
    public BigInteger create(Role role) {
        logger.info("创建新角色：code={}, name={}", role.getCode(), role.getName());
        
        // 检查角色编码是否已存在
        if (isRoleCodeExists(role.getCode())) {
            logger.warn("角色创建失败，编码已存在：code={}", role.getCode());
            throw AuthCenterMessages.ROLE_CODE_ALREADY_EXIST.createException();
        }
        roleRepository.create(role);
        
        logger.info("角色创建成功：id={}, code={}, name={}", role.getId(), role.getCode(), role.getName());
        return role.getId();
    }


    @Transactional(rollbackFor = RuntimeException.class)
    public void update(Role role) {
        logger.info("更新角色：id={}, code={}, name={}", role.getId(), role.getCode(), role.getName());
        
        // 检查角色是否存在
        var existingRole = getRole(role.getId());

        // 检查角色编码是否已被其他角色使用
        if (!existingRole.getCode().equals(role.getCode())
                && isRoleCodeExists(role.getCode())) {
            logger.warn("角色更新失败，编码已被其他角色使用：newCode={}, roleId={}", role.getCode(), role.getId());
            throw AuthCenterMessages.ROLE_CODE_ALREADY_EXIST.createException();
        }

        roleRepository.update(role);
        logger.info("角色更新成功：id={}", role.getId());
    }


    @Transactional
    public void delete(BigInteger id) {
        logger.info("删除角色：id={}", id);
        
        // 检查角色是否存在
        var role = getRole(id);

        // 逻辑删除角色（MyBatis-Plus 会自动处理 deleted 字段）
        roleRepository.delete(id);
        
        logger.info("角色删除成功：id={}", id);
    }


    private Role getRole(BigInteger id) {
        // 检查角色是否存在
        Role role = roleRepository.findById(id);
        if (role == null) {
            throw AuthCenterMessages.ROLE_NOT_EXIST.createException();
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
        return roleRepository.existsByCode(code);
    }

    public void assignRoleMenus(BigInteger id, List<BigInteger> menuIds) {
        logger.info("分配角色菜单：roleId={}, menuCount={}", id, menuIds.size());
        
        var role = roleRepository.findById(id);
        if (role == null) {
            logger.warn("角色菜单分配失败，角色不存在：roleId={}", id);
            throw AuthCenterMessages.ROLE_NOT_EXIST.createException();
        }

        // 清空角色菜单关联
        roleMenuRepository.deleteByRoleId(role.getId());


        // 重新分配角色菜单关联
        var roleMenus = menuIds.stream().map(menuId ->
                new RoleMenu(id, menuId)).collect(Collectors.toList());
        roleMenuRepository.bulkInsert(roleMenus);
        
        logger.info("角色菜单分配成功：roleId={}, menuIds={}", id, menuIds);
    }



}