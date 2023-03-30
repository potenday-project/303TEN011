package com.beside.ten011.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("my-info")
    public ResponseEntity<UserResponse> my(Authentication authentication) {
        return ResponseEntity.ok(
                UserResponse.fromEntity((User) authentication.getPrincipal()));
    }
}
