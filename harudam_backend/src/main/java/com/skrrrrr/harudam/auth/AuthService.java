package com.skrrrrr.harudam.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skrrrrr.harudam.auth.dto.ParentLoginDto;
import com.skrrrrr.harudam.auth.dto.TokenDto;
import com.skrrrrr.harudam.common.enums.UserType;
import com.skrrrrr.harudam.jwt.JwtTokenProvider;
import com.skrrrrr.harudam.member.ChildUser;
import com.skrrrrr.harudam.member.ChildUserRepository;
import com.skrrrrr.harudam.member.ParentUser;
import com.skrrrrr.harudam.member.ParentUserRepository;
import com.skrrrrr.harudam.member.dto.ChildUserDto;
import com.skrrrrr.harudam.verification.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final WebClient kakaoWebClient;
    private final ChildUserRepository childUserRepository;
    private final ParentUserRepository parentUserRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final VerificationService verificationService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 자녀 - 카카오 AccessToken 로그인 처리
     */
    public TokenDto loginWithKakaoAccessToken(String kakaoAccessToken) {
        if (kakaoAccessToken == null || kakaoAccessToken.isBlank()) {
            throw new IllegalArgumentException("kakaoAccessToken is blank");
        }

        KakaoMeResponse me;
        try {
            me = kakaoWebClient
                    .get()
                    .uri("/v2/user/me")
                    .header("Authorization", "Bearer " + kakaoAccessToken)
                    .retrieve()
                    .bodyToMono(KakaoMeResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.warn("Kakao /v2/user/me error. status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }

        if (me == null) throw new IllegalStateException("kakao profile is null");

        final String kakaoId = String.valueOf(me.id);
        final String nickname = me.properties != null ? nullToEmpty(me.properties.nickname) : "";

        // DB에서 ChildUser 조회 or 신규 생성
        ChildUser child = childUserRepository.findByUserId(kakaoId).orElseGet(() -> {
            ChildUser c = new ChildUser();
            c.setUserId(kakaoId);
            c.setName(nickname.isBlank() ? "카카오사용자" : nickname);
            return childUserRepository.save(c);
        });

        // 서버 JWT 발급
        String accessToken = jwtTokenProvider.createAccessToken(child);
        String refreshToken = jwtTokenProvider.createRefreshToken(child);

        ChildUserDto childDto = new ChildUserDto(
                child.getId(),
                child.getUserId(),
                child.getName(),
                child.getGender(),
                child.getBirth(),
                child.getPhone(),
                child.getPictureUrl(),
                child.getVoiceUrl(),
                child.getState()
        );

        return new TokenDto(
                accessToken,
                refreshToken,
                3600L,
                UserType.CHILD,
                childDto
        );
    }

    /**
     * 자녀 - 서비스 자체 로그인 (아이디/비밀번호)
     */
    public TokenDto loginWithIdPassword(String userId, String password) {
        if (userId == null || userId.isBlank() || password == null) {
            throw new IllegalArgumentException("userId/password is blank");
        }

        ChildUser child = childUserRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        String stored = child.getPassword();
        if (stored == null || !passwordEncoder.matches(password, stored)) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(child);
        String refreshToken = jwtTokenProvider.createRefreshToken(child);

        ChildUserDto childDto = new ChildUserDto(
                child.getId(),
                child.getUserId(),
                child.getName(),
                child.getGender(),
                child.getBirth(),
                child.getPhone(),
                child.getPictureUrl(),
                child.getVoiceUrl(),
                child.getState()
        );

        return new TokenDto(
                accessToken,
                refreshToken,
                3600L,
                UserType.CHILD,
                childDto
        );
    }

    /**
     * 부모 - 인증코드(8자리) 로그인
     */
    public TokenDto loginParentWithAuthCode(String authCode) {
        if (authCode == null || authCode.isBlank()) {
            throw new IllegalArgumentException("authCode is blank");
        }

        ParentUser parent = verificationService.verifyAndGetParent(authCode)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 인증코드입니다."));

        String accessToken = jwtTokenProvider.createAccessToken(parent);
        String refreshToken = jwtTokenProvider.createRefreshToken(parent);

        ParentLoginDto parentDto = new ParentLoginDto(
                parent.getId(),
                parent.getName(),
                parent.getPhone()
        );

        return new TokenDto(
                accessToken,
                refreshToken,
                3600L,
                UserType.PARENT,
                parentDto
        );
    }

    /**
     * RefreshToken으로 AccessToken 재발급 (부모/자녀 모두 지원)
     */
    public TokenDto refreshAccessToken(String refreshToken) {
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        String userType = jwtTokenProvider.getUserTypeFromToken(refreshToken);

        if ("CHILD".equals(userType)) {
            ChildUser child = childUserRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found for refresh token"));

            String newAccessToken = jwtTokenProvider.createAccessToken(child);
            String newRefreshToken = jwtTokenProvider.createRefreshToken(child);

            ChildUserDto childDto = new ChildUserDto(
                    child.getId(),
                    child.getUserId(),
                    child.getName(),
                    child.getGender(),
                    child.getBirth(),
                    child.getPhone(),
                    child.getPictureUrl(),
                    child.getVoiceUrl(),
                    child.getState()
            );

            return new TokenDto(
                    newAccessToken,
                    newRefreshToken,
                    3600L,
                    UserType.CHILD,
                    childDto
            );
        } else if ("PARENT".equals(userType)) {
            ParentUser parent = parentUserRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Parent not found for refresh token"));

            String newAccessToken = jwtTokenProvider.createAccessToken(parent);
            String newRefreshToken = jwtTokenProvider.createRefreshToken(parent);

            ParentLoginDto parentDto = new ParentLoginDto(
                    parent.getId(),
                    parent.getName(),
                    parent.getPhone()
            );

            return new TokenDto(
                    newAccessToken,
                    newRefreshToken,
                    3600L,
                    UserType.PARENT,
                    parentDto
            );
        }

        throw new IllegalArgumentException("Unknown userType in refresh token");
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    /* ===================== Kakao API 응답 매핑 DTO ===================== */
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class KakaoMeResponse {
        public long id;
        @JsonProperty("properties")
        public Properties properties;
        @JsonProperty("kakao_account")
        public KakaoAccount kakaoAccount;

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Properties {
            public String nickname;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class KakaoAccount {
            public String email;
            public String birthday;   // MMDD
            public String birthyear;  // YYYY
        }
    }
}
