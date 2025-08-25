package com.skrrrrr.harudam.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.skrrrrr.harudam.common.enums.SocialLogin;

@Repository
public interface ChildUserRepository extends JpaRepository<ChildUser, Long> {
	// 아이디로 자녀 계정 찾는 기능
	Optional<ChildUser> findByEmail(String email); 		// 소셜 매칭용
	Optional<ChildUser> findByUserId(String userId); 	// 로컬 로그인용
	boolean existsByUserId(String userId); 				// 아이디 중복 체크

	// 전화번호 중복 체크 (추가)
	boolean existsByPhone(String phone);
	
	// 소셜 로그인 정보로 자녀 계정 찾는 기능
	@Query("SELECT cu FROM ChildUser cu "
		 + "JOIN AuthIdentity ai ON ai.childUser = cu "
		 + "WHERE ai.provider = :provider "
		 + "AND ai.providerUserId = :providerUserId")
	Optional<ChildUser> findBySocialLogin(@Param("provider") SocialLogin provider, 
										  @Param("providerUserId") String providerUserId);

}