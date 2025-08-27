package com.skrrrrr.harudam.auth;

import com.skrrrrr.harudam.auth.dto.SocialLoginRequestDto;
import com.skrrrrr.harudam.auth.dto.TokenDto;
import com.skrrrrr.harudam.common.enums.SocialLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/social-login")
    public ResponseEntity<TokenDto> socialLogin(@RequestBody SocialLoginRequestDto req) {
        if (req == null || req.getProvider() == null || req.getCode() == null || req.getCode().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        TokenDto token;
        if (req.getProvider() == SocialLogin.KAKAO) {
            token = authService.loginWithKakaoAccessToken(req.getCode());
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(token);
    }

    /**
     * ✅ RefreshToken → 새 AccessToken 발급
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenDto> refresh(@RequestBody RefreshRequest req) {
        if (req == null || req.refreshToken == null || req.refreshToken.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        TokenDto token = authService.refreshAccessToken(req.refreshToken);
        return ResponseEntity.ok(token);
    }

    public static class RefreshRequest {
        public String refreshToken;
    }
}
