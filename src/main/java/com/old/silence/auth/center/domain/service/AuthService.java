package com.old.silence.auth.center.domain.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.old.silence.auth.center.api.assembler.UserMapper;
import com.old.silence.auth.center.domain.repository.RoleRepository;
import com.old.silence.auth.center.domain.repository.UserRepository;
import com.old.silence.auth.center.dto.LoginCommand;
import com.old.silence.auth.center.security.SilenceHallServerTokenAuthority;
import com.old.silence.auth.center.security.SilencePrincipal;
import com.old.silence.auth.center.vo.LoginVo;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MenuService menuService;
    private final UserMapper userMapper;
    private final SilenceHallServerTokenAuthority silenceHallServerTokenAuthority;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       MenuService menuService, UserMapper userMapper,
                       SilenceHallServerTokenAuthority silenceHallServerTokenAuthority) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.menuService = menuService;
        this.userMapper = userMapper;
        this.silenceHallServerTokenAuthority = silenceHallServerTokenAuthority;
    }

    public LoginVo login(LoginCommand request) {

        var user = userRepository.findByUsernameAndStatus(request.getUsername(), true);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!request.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        var roles = roleRepository.findRoleByUserId(user.getId());
        var principal = new SilencePrincipal(roles);
        principal.setUsername(user.getUsername());
        principal.setCnName(user.getNickname());
        principal.setUserId(user.getId());
        // 生成token
        var token = silenceHallServerTokenAuthority.issueToken(principal);
        var userInfo = userMapper.toUserVo(user);
        var loginResponse = new LoginVo();
        loginResponse.setToken(token);
        loginResponse.setUserInfo(userInfo);

        var currentUserMenuTree = menuService.getCurrentUserMenuTree(user.getId());

        loginResponse.setMenus(currentUserMenuTree);
        return loginResponse;
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }

    public LoginVo refreshToken(String refreshToken) {
       return null;
    }
} 