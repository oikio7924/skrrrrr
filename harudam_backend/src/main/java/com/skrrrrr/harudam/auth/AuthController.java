package com.skrrrrr.harudam.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skrrrrr.harudam.auth.dto.SocialLoginRequestDto;
import com.skrrrrr.harudam.auth.dto.TokenDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth") // 이 컨트롤러의 모든 api는 /api/auth로 시작
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // vue 서버의 주소 허용
public class AuthController {
	
	private final AuthService authService;
	
	@PostMapping("/social-login")
	public ResponseEntity<TokenDto> socialLogin(@RequestBody SocialLoginRequestDto requestDto) {
		TokenDto tokenDto = authService.socialLogin(requestDto);
		return ResponseEntity.ok(tokenDto);
	}

}