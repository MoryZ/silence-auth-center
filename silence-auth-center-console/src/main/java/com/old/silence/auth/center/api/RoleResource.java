package com.old.silence.auth.center.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.data.commons.converter.QueryWrapperConverter;
import com.old.silence.auth.center.api.assembler.RoleMapper;
import com.old.silence.auth.center.domain.model.Role;
import com.old.silence.auth.center.domain.service.RoleService;
import com.old.silence.auth.center.dto.RoleCommand;
import com.old.silence.auth.center.dto.RoleQuery;
import com.old.silence.auth.center.vo.RoleVo;

import java.math.BigInteger;
import java.util.List;

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
    @PreAuthorize("hasAuthority('system:role:list')")
    public Page<Role> query(Page<Role> page,RoleQuery query) {
        var queryWrapper = QueryWrapperConverter.convert(query, Role.class);
        return roleService.query(page, queryWrapper);
    }

    @GetMapping(path = "/roles", params = {"!pageNo", "!pageSize"})
    @PreAuthorize("hasAuthority('system:role:list')")
    public List<RoleVo> listAllRoles() {
        return roleService.listAllRoles();
    }

    @GetMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Role findById(@PathVariable BigInteger id) {
        return roleService.findById(id);
    }

    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('system:role:add')")
    public BigInteger create(@RequestBody RoleCommand roleCommand) {
        var role = roleMapper.convert(roleCommand);
        return roleService.create(role);
    }

    @PutMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public void update(@PathVariable BigInteger id, @RequestBody Role role) {
        role.setId(id);
        roleService.update(role);
    }

    @DeleteMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public void delete(@PathVariable BigInteger id) {
        roleService.delete(id);
    }

    @PutMapping("/roles/{id}/disable")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public void disable(@PathVariable BigInteger id) {
        roleService.updateStatus(id, false);
    }

    @PutMapping("/roles/{id}/enable")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public void enable(@PathVariable BigInteger id) {
        roleService.updateStatus(id, true);
    }

    @GetMapping("/roles/{id}/permissions")
    @PreAuthorize("hasAuthority('system:role:list')")
    public List<BigInteger> getRolePermissions(@PathVariable BigInteger id) {
        return roleService.getRoleMenuIds(id);
    }

    @PutMapping("roles/{id}/permissions")
    @PreAuthorize("hasAuthority('system:role:assign-perm')")
    public void assignRolePermissions(@PathVariable BigInteger id, @RequestBody List<BigInteger> menuIds) {
        roleService.assignRoleMenus(id, menuIds);
    }
} 