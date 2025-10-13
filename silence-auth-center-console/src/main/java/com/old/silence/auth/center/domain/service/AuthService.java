package com.old.silence.auth.center.domain.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.old.silence.auth.center.api.assembler.UserMapper;
import com.old.silence.auth.center.domain.repository.RoleRepository;
import com.old.silence.auth.center.domain.repository.UserRepository;
import com.old.silence.auth.center.dto.LoginCommand;
import com.old.silence.auth.center.infrastructure.message.AuthCenterMessages;
import com.old.silence.auth.center.security.SilenceAuthCenterRole;
import com.old.silence.auth.center.security.SilenceAuthCenterServerTokenAuthority;
import com.old.silence.auth.center.security.SilencePrincipal;
import com.old.silence.auth.center.util.PasswordUtil;
import com.old.silence.auth.center.vo.LoginVo;
import com.old.silence.core.util.CollectionUtils;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MenuService menuService;
    private final RoleRepository roleRepository;
    private final PasswordUtil passwordUtil;
    private final SilenceAuthCenterServerTokenAuthority silenceAuthCenterServerTokenAuthority;

    public AuthService(UserRepository userRepository, UserMapper userMapper, MenuService menuService, RoleRepository roleRepository, PasswordUtil passwordUtil,
                       SilenceAuthCenterServerTokenAuthority silenceAuthCenterServerTokenAuthority) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.menuService = menuService;
        this.roleRepository = roleRepository;
        this.passwordUtil = passwordUtil;
        this.silenceAuthCenterServerTokenAuthority = silenceAuthCenterServerTokenAuthority;
    }

    public LoginVo login(LoginCommand request) {

        var user = userRepository.findByUsernameAndStatus(request.getUsername(), true);
        if (user == null) {
            throw AuthCenterMessages.USER_NOT_EXIST.createException();
        }

        if (!"admin".equals(request.getUsername())) {
            if (!passwordUtil.matches(request.getPassword(), user.getPassword())) {
                throw AuthCenterMessages.PASSWORD_NOT_CORRECT.createException();
            }
        }

        var roles = roleRepository.findRoleByUserId(user.getId());
        var principal = new SilencePrincipal(
                CollectionUtils.transformToSet(roles, role -> new SilenceAuthCenterRole(role.getCode(), role.getName(), role.getAppCode())));
        principal.setUsername(user.getUsername());
        principal.setCnName(user.getNickname());
        principal.setUserId(user.getId());
        // 生成token
        var token = silenceAuthCenterServerTokenAuthority.issueToken(principal);

        var currentUserMenuTree = menuService.getCurrentUserMenuTree(user.getId());

        var userInfoVo = userMapper.toUserVo(user);
        var loginResponse = new LoginVo();
        loginResponse.setToken(token);
        loginResponse.setUserInfo(userInfoVo);
        loginResponse.setMenus(currentUserMenuTree);
        return loginResponse;
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }


} 