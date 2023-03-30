package com.beside.ten011.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OauthController {

    private final UserService userService;

    @GetMapping("oauth/kakao/redirect")
    public ResponseEntity<LoginResponse> kakaoRedirect(@RequestParam String code) {
        LoginResponse loginResponse = userService.login(code);
        return ResponseEntity.ok(loginResponse);
    }

}
