package com.beside.ten011.user.controller.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginResponse {
    private String email;
    private String nickname;
    private String token;
}
