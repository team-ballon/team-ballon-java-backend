package com.ballon.domain.auth.controller;

import com.ballon.domain.auth.dto.JwtResponse;
import com.ballon.domain.auth.dto.LoginRequest;
import com.ballon.domain.auth.service.AuthService;
import com.ballon.global.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 로그인, 토큰 재발급, 로그아웃
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public JwtResponse login(@RequestBody @Validated LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/logout")
    public void logOut(){
       authService.logOut(UserUtil.getUserId());
    }
}