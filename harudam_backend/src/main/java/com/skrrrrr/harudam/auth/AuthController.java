package com.skrrrrr.harudam.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skrrrrr.harudam.auth.dto.KakaoTokenReq;
import com.skrrrrr.harudam.auth.dto.SocialLoginRequestDto;
import com.skrrrrr.harudam.auth.dto.TokenDto;
import com.skrrrrr.harudam.common.enums.SocialLogin;
import com.skrrrrr.harudam.verification.VerificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth") // 이 컨트롤러의 모든 api는 /api/auth로 시작
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // vue 서버의 주소 허용
public class AuthController {
	
	private final AuthService authService;
	private final VerificationService verificationService;
	
	@PostMapping("/social-login")
	public ResponseEntity<TokenDto> socialLogin(@RequestBody SocialLoginRequestDto req) {
	    if (req == null || req.getProvider() == null || req.getCode() == null || req.getCode().isBlank()) {
	        return ResponseEntity.badRequest().build();
	    }

	    TokenDto token;
	    if (req.getProvider() == SocialLogin.KAKAO) {
	        // 프론트가 보내는 'code'는 카카오 accessToken이므로 이 메서드 사용
	        token = authService.loginWithKakaoAccessToken(req.getCode());
	    } else {
	        // 다른 소셜 추가 예정이면 분기 추가
	        return ResponseEntity.badRequest().build();
	    }
	    return ResponseEntity.ok(token);
	}
	
	@PostMapping("/kakao")
	public ResponseEntity<TokenDto> kakaoLogin(@RequestBody KakaoTokenReq req) {
		TokenDto token = authService.loginWithKakaoAccessToken(req.getAccessToken());
		return ResponseEntity.ok(token);
	}
	
	@PostMapping("/oauth/kakao/callback")
	public ResponseEntity<TokenDto> kakaoCallback(@RequestBody KakaoCallbackRequest req) {
	    if (req == null || req.code == null || req.code.isBlank() || req.redirectUri == null || req.redirectUri.isBlank()) {
	        return ResponseEntity.badRequest().build();
	    }
	    TokenDto token = authService.loginWithKakaoCode(req.code, req.redirectUri);
	    return ResponseEntity.ok(token);
	}

	public static class KakaoCallbackRequest {
	    public String code;
	    public String redirectUri;
	    public String state;
	}
	
	@PostMapping("/send-verification-code")
	public ResponseEntity<?> sendVerificationCode(
			@RequestParam Long childId,
			@RequestParam String phone) {
		verificationService.sendCode(childId, phone);
		return ResponseEntity.ok().body("인증번호를 전송했습니다.");
	}
	
	@PostMapping("/verify-code")
	public ResponseEntity<?> verifyCode(
			@RequestParam String phone,
			@RequestParam String code) {
		boolean success = verificationService.verifyCode(phone, code);
		if (success) {
			return ResponseEntity.ok().body("인증 성공");
		} else {
			return ResponseEntity.badRequest().body("인증 실패");
		}
	}

}