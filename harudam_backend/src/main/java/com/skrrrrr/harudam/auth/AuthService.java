package com.skrrrrr.harudam.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skrrrrr.harudam.auth.dto.TokenDto;
import com.skrrrrr.harudam.common.enums.UserType;
import com.skrrrrr.harudam.jwt.JwtTokenProvider;
import com.skrrrrr.harudam.member.ChildUser;
import com.skrrrrr.harudam.member.ChildUserRepository;
import com.skrrrrr.harudam.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Optional;

/**
 * 소셜 인증(카카오) 처리 서비스.
 * - 카카오 AccessToken 검증 → 우리 DB 저장 → 서버 Access/Refresh JWT 발급
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final WebClient kakaoWebClient;
    private final ChildUserRepository childUserRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 카카오 AccessToken 로그인 처리
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
        final String email = me.kakaoAccount != null ? nullToEmpty(me.kakaoAccount.email) : "";

        // DB에서 ChildUser 조회 or 신규 생성
        Optional<ChildUser> optionalChild = childUserRepository.findByUserId(kakaoId);
        ChildUser child = optionalChild.orElseGet(() -> {
            ChildUser c = new ChildUser();
            c.setUserId(kakaoId);
            c.setName(nickname.isBlank() ? "카카오사용자" : nickname);
            return childUserRepository.save(c);
        });

        // 서버 JWT 발급
        String accessToken = jwtTokenProvider.createAccessToken(child);
        String refreshToken = jwtTokenProvider.createRefreshToken(child);

        MemberDto member = new MemberDto(
                child.getId(),
                email,
                child.getName(),
                UserType.CHILD,
                true
        );

        return new TokenDto(
                accessToken,
                refreshToken,
                3600L,
                member
        );
    }

    /**
     * RefreshToken으로 AccessToken 재발급
     */
    public TokenDto refreshAccessToken(String refreshToken) {
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        ChildUser child = childUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for refresh token"));

        String newAccessToken = jwtTokenProvider.createAccessToken(child);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(child); // 회전(rotate) 전략

        MemberDto member = new MemberDto(
                child.getId(),
                child.getUserId(), // email 없으면 userId 대신
                child.getName(),
                UserType.CHILD,
                false
        );

        return new TokenDto(
                newAccessToken,
                newRefreshToken,
                3600L,
                member
        );
    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }

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
        }
    }
}
