package com.beside.ten011.user.controller;

import com.beside.ten011.user.controller.dto.MyRitualResponse;
import com.beside.ten011.user.controller.dto.UserResponse;
import com.beside.ten011.user.entity.User;
import com.beside.ten011.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("my-info")
    public ResponseEntity<UserResponse> myInfo(Authentication authentication) {
        return ResponseEntity.ok(
                UserResponse.fromEntity((User) authentication.getPrincipal()));
    }

    @GetMapping("my-ritual")
    public ResponseEntity<MyRitualResponse> myRitual(Authentication authentication) {
        // TODO 계산 필요
        return ResponseEntity.ok().build();
    }

    @PostMapping("logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        userService.logout(authentication);
        return ResponseEntity.ok().build();
    }

    @PostMapping("withdraw")
    public ResponseEntity<Void> withdraw(Authentication authentication) {
        userService.withdraw(authentication);
        return ResponseEntity.ok().build();
    }
}
