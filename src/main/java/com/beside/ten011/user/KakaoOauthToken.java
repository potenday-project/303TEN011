package com.beside.ten011.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class KakaoOauthToken {
    private String token_type;
    private String access_token;
    private Integer expires_in;
    private String refresh_token;
    private String refresh_token_expires_in;
    private String scope;
}
