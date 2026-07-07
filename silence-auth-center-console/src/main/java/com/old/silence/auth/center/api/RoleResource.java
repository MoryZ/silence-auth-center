package com.old.silence.auth.center.api;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

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
import com.old.silence.auth.center.api.assembler.RoleMapper;
import com.old.silence.auth.center.domain.model.Role;
import com.old.silence.auth.center.domain.model.RoleMenu;
import com.old.silence.auth.center.domain.service.RoleService;
import com.old.silence.auth.center.dto.RoleCommand;
import com.old.silence.auth.center.dto.RoleQuery;
import com.old.silence.auth.center.vo.RoleMenuView;
import com.old.silence.auth.center.vo.RoleView;
import com.old.silence.core.exception.ResourceNotFoundException;
import com.old.silence.data.commons.converter.QueryWrapperConverter;

@RestController
@RequestMapping("/api/v1")
public class RoleResource {

    private final RoleMapper roleMapper;
    private final RoleService roleService;

    public RoleResource(RoleMapper roleMapper, RoleService roleService) {
        this.roleMapper = roleMapper;
        this.roleService = roleService;
    }

    @GetMapping(value = "/roles", params = {"pageNo", "pageSize"})
    public Page<Role> queryPage(Page<Role> page, RoleQuery query) {
        var queryWrapper = QueryWrapperConverter.convert(query, Role.class);
        return roleService.queryPage(page, queryWrapper);
    }

    @GetMapping(path = "/roles", params = {"!pageNo", "!pageSize"})
    public List<RoleView> listAllRoles() {
        return roleService.listAllRoles();
    }

    @GetMapping(path = "/roles/minimum")
    public List<RoleView> minimumRoles() {
        return roleService.minimumRoles();
    }

    @GetMapping("/roles/{id}")
    public RoleView findById(@PathVariable BigInteger id) {
        return roleService.findById(id, RoleView.class)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping("/roles")
    public BigInteger create(@RequestBody @Validated RoleCommand roleCommand) {
        var role = roleMapper.convert(roleCommand);
        return roleService.create(role);
    }

    @PutMapping("/roles/{id}")
    public void update(@PathVariable BigInteger id, @RequestBody RoleCommand roleCommand) {
        var role = roleMapper.convert(roleCommand);
        role.setId(id);
        roleService.update(role);
    }

    @DeleteMapping("/roles/{id}")
    public void delete(@PathVariable BigInteger id) {
        roleService.delete(id);
    }

    @PutMapping("/roles/{id}/disable")
    public void disable(@PathVariable BigInteger id) {
        roleService.updateStatus(id, false);
    }

    @PutMapping("/roles/{id}/enable")
    public void enable(@PathVariable BigInteger id) {
        roleService.updateStatus(id, true);
    }

    @GetMapping("/roles/{id}/permissions")
    public List<RoleMenuView> getRolePermissions(@PathVariable BigInteger id) {
        var roleMenuViews = roleService.findById(id, RoleView.class);
        return roleMenuViews.map(RoleView::getRoleMenus)
                .orElse(null);
    }

    @PutMapping("roles/{id}/permissions")
    public void assignRolePermissions(@PathVariable BigInteger id, @RequestBody List<BigInteger> menuIds) {
        roleService.assignRoleMenus(id, menuIds);
    }
} 