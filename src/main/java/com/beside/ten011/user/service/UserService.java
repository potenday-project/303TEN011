package com.beside.ten011.user.service;

import com.beside.ten011.exception.CustomException;
import com.beside.ten011.exception.ErrorCode;
import com.beside.ten011.user.controller.dto.LoginResponse;
import com.beside.ten011.user.entity.User;
import com.beside.ten011.user.repository.UserRepository;
import com.beside.ten011.user.service.dto.KakaoOauthToken;
import com.beside.ten011.user.service.dto.KakaoProfile;
import com.beside.ten011.util.JwtTokenUtils;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserService {

    private final UserRepository userRepository;

    @Value("${social.kakao.url.api-host}")
    private String kakaoHostUrl;

    @Value("${social.kakao.url.token}")
    private String kakaoTokenUrl;

    @Value("${social.kakao.url.profile}")
    private String kakaoProfileUrl;

    @Value("${social.kakao.redirect}")
    private String kakaoRedirectUrl;

    @Value("${social.kakao.client-id}")
    private String kakaoClientId;

    @Value("${url.base}")
    private String baseUrl;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;


    /**
     * 로그인
     *
     * @param code
     * @return
     */
    @Transactional
    public LoginResponse login(String code) {
        KakaoOauthToken kaKaoAccessToken = getKaKaoAccessToken(code);
        KakaoProfile kakaoProfile = getUserProfile(kaKaoAccessToken);

        // 카카오 프로필 정보(이메일)로 회원 여부 확인
        User user = userRepository.findByEmail(kakaoProfile.getKakao_account().getEmail())
                .orElseGet(() -> {
                    // 회원이 아니라면 회원가입
                    return userRepository.save(
                            User.builder()
                                    .email(kakaoProfile.getKakao_account().getEmail())
                                    .nickname(kakaoProfile.getProperties().getNickname())
                                    .build());
                });

        // jwt 발급
        String token = JwtTokenUtils.generateToken(user.getEmail(), secretKey, expiredTimeMs);

        // 로그인 처리
        //        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(kakaoUser.getUsername(), yjKey));
        //        SecurityContextHolder.getContext().setAuthentication(authentication);

        return LoginResponse.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .token(token)
                .build();
    }

    /**
     * 카카오 사용자 정보 가져오기
     *
     * @param oauthToken
     * @return
     */
    private KakaoProfile getUserProfile(KakaoOauthToken oauthToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer " + oauthToken.getAccess_token());

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                kakaoHostUrl + kakaoProfileUrl,
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            Gson gson = new Gson();
            return gson.fromJson(response.getBody(), KakaoProfile.class);
        } else {
            log.error(response.getStatusCode().toString());
            log.error(response.getBody());
            throw new CustomException(ErrorCode.KAKAO_PROFILE_ERROR);
        }
    }

    /**
     * 카카오 액세스 토큰 받기
     *
     * @param code
     * @return
     */
    public KakaoOauthToken getKaKaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code"); // 고정
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", baseUrl + kakaoRedirectUrl);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                kakaoTokenUrl,
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            Gson gson = new Gson();
            return gson.fromJson(response.getBody(), KakaoOauthToken.class);
        } else {
            throw new CustomException(ErrorCode.KAKAO_TOKEN_ERROR);
        }
    }


    public User loadUserByUserName(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
