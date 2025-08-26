package com.skrrrrr.harudam.auth;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.skrrrrr.harudam.auth.dto.SocialLoginRequestDto;
import com.skrrrrr.harudam.auth.dto.TokenDto;
import com.skrrrrr.harudam.jwt.JwtTokenProvider;
import com.skrrrrr.harudam.member.ChildUser;
import com.skrrrrr.harudam.member.ChildUserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	
	private final ChildUserRepository childUserRepository;
	private final AuthIdentityRepository authIdentityRepository;
	private final JwtTokenProvider jwtTokenProvider;
	
	@Transactional
	public TokenDto socialLogin(SocialLoginRequestDto requestDto) {
		// 1. 프론트에서 받은 인가 코드로 소셜 API를 호출하여 사용자 정보를 받아온다.
		// (이 부분은 실제 소셜 로그인 SDK나 RestTemplate/WebClient를 사용해 구현해야 합니다.)
        // SocialUserInfo socialUserInfo = getSocialUserInfo(requestDto.getProvider(), requestDto.getCode());
		
		// ---- 테스트 임시 사용자 정보 ----
		// 실제 구현 시에는 위 getSocialUserInfo 메서드의 결과값을 사용해야 합니다.
        String providerUserId = "test-social-id-12345"; // 예: 카카오에서 받은 유저 고유 ID
        String userEmail = "test@example.com";
        String userName = "테스트유저";
        // --- 테스트 코드 끝 ---
        
        // 2. 소셜 ID로 이미 가입된 사용자인지 확인한다.
        Optional<AuthIdentity> existingIdentity = authIdentityRepository.findByProviderAndProviderUserId(
        		requestDto.getProvider(),
        		providerUserId
		);
        
        // 3. 로그인 & 회원가입 if 문
        ChildUser childUser;
        if (existingIdentity.isPresent()) {
        	// 이미 가입이 된 경우 (로그인)
        	childUser = existingIdentity.get().getChildUser();
        } else {
        	// 처음 로그인하는 경우 (회원가입)
        	ChildUser newChild = new ChildUser();
        	newChild.setName(userName);
        	// newChild.setPhone(socialUserInfo.getPhonNumber()); // 소셜 정보에 따라 추가 정보 설정
        	childUser = childUserRepository.save(newChild);
        	
        	AuthIdentity newIdentity = new AuthIdentity();
        	newIdentity.setProvider(requestDto.getProvider());
        	newIdentity.setProviderUserId(providerUserId);
        	newIdentity.setProviderEmail(userEmail);
        	newIdentity.setDisplayName(userName);
        	newIdentity.setChildUser(childUser);
        	authIdentityRepository.save(newIdentity);
        }	
        // 4. 우리 서비스의 JWT 토큰을 생성하여 반환한다.
        // String accessToken = jwtTokenProvider.createToken(childUser.getId(), childUser.getRole());
        String accessToken = jwtTokenProvider.createAccessToken(childUser); // JWT구현 전 임시 토큰	
        
        return new TokenDto(accessToken);
        	
	}
}