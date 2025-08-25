package com.skrrrrr.harudam.auth;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.skrrrrr.harudam.auth.dto.ParentCodeIssueResponseDto;
import com.skrrrrr.harudam.auth.dto.TokenDto;
import com.skrrrrr.harudam.auth.provider.SocialVerifier;
import com.skrrrrr.harudam.common.enums.AuthCodeStatus;
import com.skrrrrr.harudam.common.enums.OnboardingState;
import com.skrrrrr.harudam.common.enums.SocialLogin;
import com.skrrrrr.harudam.common.enums.UserStatus;
import com.skrrrrr.harudam.common.enums.UserType;
import com.skrrrrr.harudam.jwt.JwtTokenProvider;
import com.skrrrrr.harudam.member.ChildUser;
import com.skrrrrr.harudam.member.ChildUserRepository;
import com.skrrrrr.harudam.member.ParentChildLink;
import com.skrrrrr.harudam.member.ParentChildLinkRepository;
import com.skrrrrr.harudam.member.ParentUserRepository;
import com.skrrrrr.harudam.member.dto.UserDto;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthCodeRepository authCodeRepository;
	private final ChildUserRepository childUserRepository;
	private final ParentUserRepository parentUserRepository;
	private final ParentChildLinkRepository parentChildLinkRepository;
	private final AuthIdentityRepository authIdentityRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final SocialVerifier socialVerifier;


//    AuthService(AuthCodeRepository authCodeRepository) {
//        this.authCodeRepository = authCodeRepository;
//    }
	

	@Transactional
	public TokenDto socialLogin(SocialLogin provider, String accessToken) {
		
		// 1. 공급자 access_token 검증 -> 프로필 획득
		var profile = socialVerifier.verify(provider, accessToken);
		// p: providerUserId, email(옵션), nickname 포함
        
        // 2. 이미 연결된 소셜 계정이 있나 확인
        Optional<AuthIdentity> existingIdentity =
        		authIdentityRepository.findByProviderAndProviderUserId(provider, profile.providerUserId());
        
        // 3. 로그인 & 회원가입 if 문
        ChildUser childUser;
        if (existingIdentity.isPresent()) {
        	// 로그인
        	AuthIdentity identity = existingIdentity.get();
        	childUser = identity.getChildUser();
        	identity.setLastLoginAt(ZonedDateTime.now());
        } else {
        	// 이메일로 기존 사용자 매칭
        	childUser = (profile.email() != null)
        			? childUserRepository.findByEmail(profile.email()).orElse(null)
        			: null;
        	
        	if (childUser == null) {
        		childUser = new ChildUser();
        		childUser.setNickname(profile.nickname());
        		childUser.setEmail(profile.email());
        		childUser.setStatus(UserStatus.PENDING);
        		childUser.setUserType(UserType.CHILD);
        		childUser = childUserRepository.save(childUser);
        	}

        	AuthIdentity newIdentity = new AuthIdentity();
        	newIdentity.setProvider(provider);
        	newIdentity.setProviderUserId(profile.providerUserId());
        	newIdentity.setProviderEmail(profile.email());
        	newIdentity.setDisplayName(profile.nickname());
        	newIdentity.setChildUser(childUser);
        	newIdentity.setConnectedAt(ZonedDateTime.now());
        	authIdentityRepository.save(newIdentity);
        	
        	log.info("저장된 ChildUser: {}", childUser.getId());
        	log.info("저장된 AuthIdentity: {}", newIdentity.getIdentityId());
        }	

        // 4. 우리 서비스의 JWT 토큰을 생성하여 반환한다.
        String access = jwtTokenProvider.createAccessToken(childUser);
        String refresh = jwtTokenProvider.createRefreshToken(childUser);
        long expiresIn = jwtTokenProvider.getAccessTokenExpiresInSeconds();
        
        return new TokenDto(
        	    access,
        	    refresh,
        	    expiresIn,
        	    new UserDto(childUser.getId(), childUser.getEmail(), childUser.getName(),
        	                "PENDING".equals(childUser.getStatus() == UserStatus.PENDING)));
        	}
	
	@Transactional
	public ParentCodeIssueResponseDto issueParentCode(Long childId, String parentPhone) {
		// 1. 자녀 확인
		ChildUser child = childUserRepository.findById(childId)
				.orElseThrow(() -> new EntityNotFoundException("ChildUser not fonud: id=" + childId));
		
		// 2. 6자리 랜덤 코드 생성 (대문자+숫자)
		String code = generateCode();
		
		// 3. 만료 시간 설정 (예: 10분 뒤)
		ZonedDateTime expiresAt = ZonedDateTime.now().plusMinutes(10);
		
		// 4. AuthCode 엔티티 생성 및 저장
		AuthCode authCode = new AuthCode();
		authCode.setCodeValue(code);
		authCode.setIssuedByChild(child);
		authCode.setTargetParentPhone(parentPhone);
		authCode.setExpiresAt(expiresAt);
		authCode.setStatus(AuthCodeStatus.ISSUED);
		authCodeRepository.save(authCode);
		
		// 5. 응답 반환
		return new ParentCodeIssueResponseDto(code, expiresAt);
	}
	
	// 랜덤 6자리 코드 생성
	private String generateCode() {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<6; i++) {
			int idx = (int) (Math.random() * chars.length());
			sb.append(chars.charAt(idx));
		}
		return sb.toString();
	}
	
	@Transactional
	public boolean verifyParentCode(String coedValue) {
		// 코드 조회 (ISSUED 상태만)
		AuthCode authCode = authCodeRepository.findByCodeValueAndStatus(coedValue, AuthCodeStatus.ISSUED)
				.orElseThrow(() -> new IllegalArgumentException("유효하지 않거나 만료된 코드입니다."));
		
		// 만료 확인
		if (authCode.getExpiresAt().isBefore(ZonedDateTime.now())) {
			authCode.setStatus(AuthCodeStatus.EXPIRED);
			authCodeRepository.save(authCode);
			return false;
		}
		
		// 부모 조회
		var parent = parentUserRepository.findByPhone(authCode.getTargetParentPhone())
				.orElseThrow(() -> new EntityNotFoundException("부모 계정을 찾을 수 없습니다. phone=" + authCode.getTargetParentPhone()));
		
		// 코드 사용 처리
		authCode.setStatus(AuthCodeStatus.USED);
		authCode.setUsedAt(ZonedDateTime.now());
		authCode.setUsedByParent(parent);
		authCodeRepository.save(authCode);

		// ParentChildLink 생성 (자녀 ↔ 부모 연결)
		ParentChildLink link = new ParentChildLink();
		link.setChildUser(authCode.getIssuedByChild());
		link.setParentUser(parent);
		parentChildLinkRepository.save(link);
		
		// 자녀 상태 업데이트
		ChildUser child = authCode.getIssuedByChild();
		child.setOnboardingState(OnboardingState.PARENT_LINKED);
		child.setStatus(UserStatus.ACTIVE);
		childUserRepository.save(child);
		
		// 부모 활성화
		parent.setLastConversationAt(ZonedDateTime.now());
		parent.setStatus(UserStatus.ACTIVE);
		parentUserRepository.save(parent);
		
		return true;
	}
}
	