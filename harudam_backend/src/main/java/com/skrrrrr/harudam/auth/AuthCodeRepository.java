package com.skrrrrr.harudam.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skrrrrr.harudam.common.enums.AuthCodeStatus;

@Repository
public interface AuthCodeRepository extends JpaRepository<AuthCode, Long>{
	
	// 코드값으로 검색
	Optional<AuthCode> findByCodeValue(String codeValue);
	
	// 상태 포함해서 검색 (예 : ISSUED 상태 코드만 찾기)
	Optional<AuthCode> findByCodeValueAndStatus(String codeValue, AuthCodeStatus status);
}