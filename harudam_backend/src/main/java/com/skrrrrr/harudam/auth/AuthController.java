package com.skrrrrr.harudam.auth;

import com.skrrrrr.harudam.auth.dto.SocialLoginRequestDto;
import com.skrrrrr.harudam.auth.dto.TokenDto;
import com.skrrrrr.harudam.common.dto.ApiResponse;
import com.skrrrrr.harudam.common.enums.SocialLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    /**
     * 자녀 소셜 로그인 (Kakao/Google/Naver)
     */
    @PostMapping("/social-login")
    public ResponseEntity<ApiResponse<TokenDto>> socialLogin(@RequestBody SocialLoginRequestDto req) {
        if (req == null || req.getProvider() == null || req.getCode() == null || req.getCode().isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("INVALID_REQUEST"));
        }

        TokenDto token;
        if (req.getProvider() == SocialLogin.KAKAO) {
            token = authService.loginWithKakaoAccessToken(req.getCode());
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.fail("UNSUPPORTED_SOCIAL_PROVIDER"));
        }
        return ResponseEntity.ok(ApiResponse.ok("LOGIN_SUCCESS", token));
    }

    /**
     * RefreshToken → 새 AccessToken 발급
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenDto>> refresh(@RequestBody RefreshRequest req) {
        if (req == null || req.refreshToken == null || req.refreshToken.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("REFRESH_TOKEN_REQUIRED"));
        }
        TokenDto token = authService.refreshAccessToken(req.refreshToken);
        return ResponseEntity.ok(ApiResponse.ok("TOKEN_REFRESH_SUCCESS", token));
    }

    /**
     * 부모 로그인 - 인증코드만 입력
     */
    @PostMapping("/parent-login")
    public ResponseEntity<ApiResponse<TokenDto>> parentLogin(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        if (code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("AUTH_CODE_REQUIRED"));
        }
        TokenDto token = authService.loginParentWithAuthCode(code);
        return ResponseEntity.ok(ApiResponse.ok("LOGIN_SUCCESS", token));
    }

    /**
     * 로그아웃
     * - JWT는 서버 세션이 없으므로, 실제로는 클라이언트가 토큰을 삭제하면 됨
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Map<String, String>>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("INVALID_AUTH_HEADER"));
        }

        return ResponseEntity.ok(ApiResponse.ok("LOGOUT_SUCCESS",
                Map.of("message", "클라이언트 토큰 삭제 필요")));
    }

    public static class RefreshRequest {
        public String refreshToken;
    }
}
