package com.beside.ten011.user.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class LoginResponse {
    private String email;
    private String nickname;
    private String token;
}
