package com.old.silence.auth.center.api;

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
import com.old.silence.auth.center.dto.ResetUserPasswordCommand;
import com.old.silence.auth.center.dto.UserCommand;
import com.old.silence.auth.center.dto.UserQuery;
import com.old.silence.auth.center.infrastructure.message.AuthCenterMessages;
import com.old.silence.auth.center.security.SilenceAuthCenterContextHolder;
import com.old.silence.auth.center.vo.UserView;
import com.old.silence.core.exception.ResourceNotFoundException;
import com.old.silence.data.commons.converter.QueryWrapperConverter;

import java.math.BigInteger;

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
    public IPage<UserView> query(Page<User> page, UserQuery query) {
        var queryWrapper = QueryWrapperConverter.convert(query, User.class);
        return userService.query(page, queryWrapper, UserView.class);
    }

    @GetMapping("/users/{id}")
    public UserView findById(@PathVariable BigInteger id) {
        return userService.findById(id, UserView.class).orElseThrow(ResourceNotFoundException::new);
    }

    @GetMapping("/users/info")
    public UserView getCurrentUserInfo() {
        var userId = SilenceAuthCenterContextHolder.getAuthenticatedUserId()
                .orElseThrow(AuthCenterMessages.USER_NOT_EXIST::createException);

        return userService.findById(userId, UserView.class)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping("/users")
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
    public void update(@PathVariable BigInteger id, @RequestBody UserCommand userCommand) {
        var user = userMapper.convert(userCommand);
        user.setId(id); //NO SONAR
        userService.update(user);
    }

    @PutMapping("/users/{id}/disable")
    public void disable(@PathVariable BigInteger id) {
        userService.updateUserStatus(id, false);
    }

    @PutMapping("/users/{id}/enable")
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


    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable BigInteger id) {
        userService.delete(id);
    }

} 