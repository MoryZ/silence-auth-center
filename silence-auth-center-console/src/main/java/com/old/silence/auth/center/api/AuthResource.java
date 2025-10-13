package com.old.silence.auth.center.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.old.silence.auth.center.domain.service.AuthService;
import com.old.silence.auth.center.dto.LoginCommand;
import com.old.silence.auth.center.vo.LoginVo;

@RestController
@RequestMapping("/api/v1")
public class AuthResource {

    private final AuthService authService;

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/login")
    public LoginVo login(@RequestBody @Validated LoginCommand request) {
        return authService.login(request);
    }

    @PostMapping("/auth/logout")
    public void logout() {
        authService.logout();
    }

} 