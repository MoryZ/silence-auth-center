package com.old.silence.auth.center.api;

import jakarta.validation.constraints.NotEmpty;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.auth.center.api.assembler.UserMapper;
import com.old.silence.auth.center.domain.model.User;
import com.old.silence.auth.center.domain.service.UserService;
import com.old.silence.auth.center.dto.ModifyUserPasswordCommand;
import com.old.silence.auth.center.dto.UserCommand;
import com.old.silence.auth.center.dto.ResetUserPasswordCommand;
import com.old.silence.auth.center.dto.UserQuery;
import com.old.silence.auth.center.vo.UserVo;
import com.old.silence.data.commons.converter.QueryWrapperConverter;

@RestController
@RequestMapping("/api/v1")
public class UserResource {

    private final UserMapper userMapper;
    private final UserService userService;

    public UserResource(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @GetMapping("/users/{id}/roles")
    @PreAuthorize("@perm.hasAuthority('system:user:list')")
    public List<BigInteger> getUserRoleIds(@PathVariable BigInteger id) {
        return userService.getUserRoleIds(id);
    }

    @GetMapping(value = "/users", params = {"pageNo", "pageSize"})
    @PreAuthorize("@perm.hasAuthority('system:user:page')")
    public IPage<UserVo> query(Page<User> page, UserQuery query) {
        var queryWrapper = QueryWrapperConverter.convert(query, User.class);
        var userPage = userService.query(page, queryWrapper);
        return userPage.convert(userMapper::toUserVo);
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("@perm.hasAuthority('system:user:list')")
    public UserVo getUserById(@PathVariable BigInteger id) {
        User user = userService.findById(id);
        return userMapper.toUserVo(user);
    }

    @PostMapping("/users")
    @PreAuthorize("@perm.hasAuthority('system:user:add')")
    public BigInteger create(@RequestBody UserCommand userCommand) {
        var user = userMapper.convert(userCommand);
        return userService.create(user); // NO SONAR
    }

    @PostMapping("/users/register")
    public BigInteger register(@RequestBody UserCommand userCommand) {
        var user = userMapper.convert(userCommand);
        return userService.register(user); // NO SONAR
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("@perm.hasAuthority('system:user:edit')")
    public void update(@PathVariable BigInteger id, @RequestBody UserCommand userCommand) {
        var user = userMapper.convert(userCommand);
        user.setId(id); //NO SONAR
        userService.update(user);
    }

    @PutMapping("/users/{id}/disable")
    @PreAuthorize("@perm.hasAuthority('system:user:edit')")
    public void disable(@PathVariable BigInteger id) {
        userService.updateUserStatus(id, false);
    }

    @PutMapping("/users/{id}/enable")
    @PreAuthorize("@perm.hasAuthority('system:user:edit')")
    public void enable(@PathVariable BigInteger id) {
        userService.updateUserStatus(id, true);
    }

    @PutMapping("/users/{id}/resetPassword")
    public void resetPassword(@PathVariable BigInteger id, @RequestBody @Validated ResetUserPasswordCommand resetUserPasswordCommand) {
        userService.resetPassword(id, resetUserPasswordCommand.getNewPassword());
    }

    @PutMapping("/users/modifyPassword")
    public void modifyPassword(@RequestBody @Validated ModifyUserPasswordCommand modifyUserPasswordCommand) {
        userService.modifyPassword(modifyUserPasswordCommand.getUsername(), modifyUserPasswordCommand.getNewPassword());
    }

    @PutMapping("/users/{id}/roles")
    @PreAuthorize("@perm.hasAuthority('system:user:edit')")
    public void assignUserRoles(@PathVariable BigInteger id, @RequestBody @NotEmpty Set<BigInteger> roleIds) {
        userService.assignUserRoles(id, roleIds);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("@perm.hasAuthority('system:user:delete')")
    public void deleteUser(@PathVariable BigInteger id) {
        userService.delete(id);
    }

} 