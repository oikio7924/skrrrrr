package com.skrrrrr.harudam.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skrrrrr.harudam.auth.dto.TokenDto;
import com.skrrrrr.harudam.member.dto.MemberDto;
import com.skrrrrr.harudam.common.enums.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * 소셜 인증(카카오) 처리 서비스.
 * - 프론트가 보낸 카카오 accessToken을 사용해 사용자 프로필(/v2/user/me) 조회
 * - 우리 쪽에서 사용하기 편한 MemberDto로 변환
 * - TokenDto에 accessToken/refreshToken을 담아 반환
 *
 * ⚠️ 현재는 DB 저장 없이 동작하도록 최소 구현(에러 제거 목적).
 *    이후 ParentUser/ChildUser 확정되면 find-or-create 로직을 이곳에 추가하면 됨.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    /** HttpClientsConfig 에서 등록한 빈 (baseUrl: https://kapi.kakao.com) */
    private final WebClient kakaoWebClient;

    /**
     * 프론트에서 보낸 "카카오 accessToken" 으로 카카오 유저 정보를 조회하고 우리 TokenDto 로 반환.
     * @param kakaoAccessToken 프론트에서 받은 카카오 access token
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
            // 카카오에서 401/403 등 내려줄 때
            log.warn("Kakao /v2/user/me error. status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }

        if (me == null) {
            throw new IllegalStateException("kakao profile is null");
        }

        // 프로필 파싱
        final String kakaoId = String.valueOf(me.id);
        final String nickname = me.properties != null ? nullToEmpty(me.properties.nickname) : "";
        final String email = me.kakaoAccount != null ? nullToEmpty(me.kakaoAccount.email) : "";

        // 우리 쪽 멤버 표현 (우선 Parent 가정, 추가정보 입력 필요 여부는 이메일/기본정보 존재 여부로 판단)
        MemberDto member = new MemberDto(
                null,                      // 아직 DB 저장 안 하므로 id 없음
                email,
                nickname.isBlank() ? "카카오사용자" : nickname,
                UserType.PARENT,           // 필요 시 CHILD 로 변경/분기
                true                       // 추가정보 입력 필요 플래그 (온보딩 화면 유도)
        );

        // accessToken/refreshToken 은 우선 프론트가 저장하고 다음 화면으로 넘어가도록만 반환
        // (실제 JWT 발급은 JwtTokenProvider 연동 확정 후 교체)
        long expiresInSec = 3600L; // 프론트 표시에 사용할 만료(표시용)
        return new TokenDto(
                kakaoAccessToken,   // 임시로 카카오 토큰을 accessToken 자리에 전달 (프론트 흐름 유지)
                "",                 // refreshToken 미사용
                expiresInSec,
                member
        );
    }

    /**
     * (선택) "인가 코드"로 로그인하는 흐름이 필요하면 여기서 교환까지 수행
     * - 현재 FE는 accessToken을 직접 보내므로 사용되지 않음.
     */
    public TokenDto loginWithKakaoCode(String code, String redirectUri) {
        throw new UnsupportedOperationException("카카오 인가코드 교환 흐름은 현재 사용하지 않음");
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
            @JsonProperty("profile_image")
            public String profileImage;
            @JsonProperty("thumbnail_image")
            public String thumbnailImage;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class KakaoAccount {
            public String email;
        }
    }
}
