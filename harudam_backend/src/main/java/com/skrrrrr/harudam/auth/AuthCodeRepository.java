package com.skrrrrr.harudam.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skrrrrr.harudam.common.enums.AuthCodeStatus;

@Repository
public interface AuthCodeRepository extends JpaRepository<AuthCode, Long> {
	// 전화번호로 최신 코드 조회 시 사용
    Optional<AuthCode> findTopByTargetParentPhoneAndStatusOrderByCreatedAtDesc(
            String targetParentPhone,
            AuthCodeStatus status
    );
    // 코드 값으로 직접 조회하여 로그인 시 사용
    Optional<AuthCode> findByCodeValueAndStatus(
    		String codeValue, AuthCodeStatus status);

    
}
