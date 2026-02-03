package com.old.silence.auth.center.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.old.silence.auth.center.api.assembler.UserMapper;
import com.old.silence.auth.center.domain.repository.RoleRepository;
import com.old.silence.auth.center.domain.repository.UserRepository;
import com.old.silence.auth.center.dto.LoginCommand;
import com.old.silence.auth.center.dto.MenuDto;
import com.old.silence.auth.center.enums.MenuType;
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

        if (!passwordUtil.matches(request.getPassword(), user.getPassword())) {
            throw AuthCenterMessages.PASSWORD_NOT_CORRECT.createException();
        }

        List<MenuDto> currentUserMenuTree = menuService.getCurrentUserMenuTree(user.getId());
        var permissions = flattenMenu(currentUserMenuTree);


        var roles = roleRepository.findRoleByUserId(user.getId());
        var principal = new SilencePrincipal(
                CollectionUtils.transformToSet(roles, role -> new SilenceAuthCenterRole(role.getCode(), role.getName(), role.getAppCode())),
                permissions
        );
        principal.setUsername(user.getUsername());
        principal.setCnName(user.getNickname());
        principal.setUserId(user.getId());
        // 生成token
        var token = silenceAuthCenterServerTokenAuthority.issueToken(principal);

        var userInfoVo = userMapper.toUserVo(user);
        userInfoVo.setPermissions(permissions);
        var loginResponse = new LoginVo();
        loginResponse.setToken(token);
        loginResponse.setUserInfo(userInfoVo);
        loginResponse.setMenus(currentUserMenuTree);
        return loginResponse;
    }

    private Set<String> flattenMenu(List<MenuDto> currentUserMenuTree) {
        return currentUserMenuTree.stream()
                .flatMap(menu -> {
                    List<MenuDto> result = new ArrayList<>();
                    Stack<MenuDto> stack = new Stack<>();
                    stack.push(menu);

                    while (!stack.isEmpty()) {
                        MenuDto current = stack.pop();
                        result.add(current);

                        // 将子节点逆序压入栈中，保持原有顺序
                        if (current.getChildren() != null && !current.getChildren().isEmpty()) {
                            for (int i = current.getChildren().size() - 1; i >= 0; i--) {
                                stack.push(current.getChildren().get(i));
                            }
                        }
                    }

                    return result.stream();
                })
                .filter(menu -> menu.getType() == MenuType.BUTTON)
                .map(menu -> (String) menu.getMeta().get("permission"))
                .collect(Collectors.toSet());
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }


} 