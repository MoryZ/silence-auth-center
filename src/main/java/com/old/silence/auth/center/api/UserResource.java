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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.old.silence.auth.center.api.assembler.UserMapper;
import com.old.silence.auth.center.domain.model.User;
import com.old.silence.auth.center.domain.service.UserService;
import com.old.silence.auth.center.dto.UserCommand;
import com.old.silence.auth.center.dto.UserQuery;
import com.old.silence.auth.center.vo.UserVo;
import com.old.silence.data.commons.converter.QueryWrapperConverter;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1")
public class UserResource {

    private final UserMapper userMapper;
    private final UserService userService;

    public UserResource(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @GetMapping(value = "/users", params = {"pageNo", "pageSize"})
    @PreAuthorize("hasAuthority('system:user:list')")
    public IPage<UserVo> query(Page<User> page, UserQuery query) {
        var queryWrapper = QueryWrapperConverter.convert(query, User.class);
        var userPage = userService.query(page, queryWrapper);
        return userPage.convert(userMapper::toUserVo);
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAuthority('system:user:list')")
    public User getUserById(@PathVariable BigInteger id) {
        return userService.findById(id);
    }

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('system:user:add')")
    public BigInteger create(@RequestBody UserCommand userCommand) {
        var user = userMapper.convert(userCommand);
        return userService.create(user); // NO SONAR
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public void update(@PathVariable BigInteger id, @RequestBody UserCommand userCommand) {
        var user = userMapper.convert(userCommand);
        user.setId(id); //NO SONAR
        userService.update(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public void deleteUser(@PathVariable BigInteger id) {
        userService.delete(id);
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public void disable(@PathVariable BigInteger id) {
        userService.updateUserStatus(id, false);
    }

    @PutMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public void enable(@PathVariable BigInteger id) {
        userService.updateUserStatus(id, true);
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasAuthority('system:user:reset-password')")
    public void resetPassword(@PathVariable BigInteger id, @RequestParam String password) {
        userService.resetPassword(id, password);
    }

    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('system:user:list')")
    public List<BigInteger>getUserRoleIds(@PathVariable BigInteger id) {
        return userService.getUserRoleIds(id);
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public void assignUserRoles(@PathVariable BigInteger id, @RequestBody Set<BigInteger> roleIds) {
        userService.assignUserRoles(id, roleIds);
    }
} 