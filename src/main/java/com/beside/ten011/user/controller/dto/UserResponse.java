package com.beside.ten011.user.controller.dto;

import com.beside.ten011.user.entity.User;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserResponse {
    private String email;
    private String nickname;

    public static UserResponse fromEntity(User user) {
        return new UserResponse(user.getEmail(), user.getNickname());
    }
}
