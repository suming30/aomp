package com.lsm.aomp.controller;

import com.lsm.aomp.common.Result;
import com.lsm.aomp.dto.LoginDTO;
import com.lsm.aomp.service.AuthService;
import com.lsm.aomp.util.UserContext;
import com.lsm.aomp.vo.LoginVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        return authService.login(dto, ip);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        String userId = UserContext.getCurrentUserId();
        return authService.logout(userId);
    }

    @PostMapping("/refresh")
    public Result<LoginVO> refreshToken() {
        String userId = UserContext.getCurrentUserId();
        return authService.refreshToken(userId);
    }
}
