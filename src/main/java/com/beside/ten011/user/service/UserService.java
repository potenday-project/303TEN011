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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserService {

    private final UserRepository userRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${social.kakao.url.api-host}")
    private String kakaoHostUrl;

    @Value("${social.kakao.url.token}")
    private String kakaoTokenUrl;

    @Value("${social.kakao.url.profile}")
    private String kakaoProfileUrl;

    @Value("${social.kakao.url.unlink}")
    private String kakaoUnlinkUrl;

    @Value("${social.kakao.redirect}")
    private String kakaoRedirectUrl;

    @Value("${social.kakao.admin-key}")
    private String kakaoAdminKey;

    @Value("${social.kakao.client-id}")
    private String kakaoClientId;

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
                                    .kakaoId(kakaoProfile.getId())
                                    .build());
                });

        // TODO 임시코드 추후 삭제 필요
        user.tmpUpdateKakaoId(kakaoProfile.getId());

        // jwt 발급
        String token = JwtTokenUtils.generateToken(user.getEmail(), secretKey, expiredTimeMs);

        //redis에 RT:이메일(key) / 토큰(value) 형태로 리프레시 토큰 저장하기
        redisTemplate.opsForValue().set("RT:" + user.getEmail(), token, expiredTimeMs, TimeUnit.MILLISECONDS);

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
        params.add("redirect_uri", kakaoRedirectUrl);
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

    public void logout(Authentication authentication) {
        // Redis에서 해당 User email로 저장된 Token 이 있는지 여부를 확인 후에 있을 경우 삭제를 한다.
        if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            // Token을 삭제
            redisTemplate.delete("RT:" + authentication.getName());
        }
    }

    /**
     * 회원탈퇴
     *
     * @param authentication
     */
    @Transactional
    public void withdraw(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        // 로그아웃
        logout(authentication);
        // 카카오 연결 해지
        unlinkKakao(user.getKakaoId());
        // 회원 탈퇴
        userRepository.deleteByEmail(authentication.getName());
    }

    /**
     * 카카오 연결 끊기
     *
     * @param kakaoId
     * @return
     */
    public boolean unlinkKakao(Long kakaoId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + kakaoAdminKey);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("target_id_type", "user_id"); // 고정
        params.add("target_id", String.valueOf(kakaoId));

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                kakaoHostUrl + kakaoUnlinkUrl,
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            return true;
        } else {
            throw new CustomException(ErrorCode.KAKAO_TOKEN_ERROR);
        }
    }
}
