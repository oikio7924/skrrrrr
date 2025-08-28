package com.skrrrrr.harudam.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.skrrrrr.harudam.common.enums.SocialLogin;

@Repository
public interface ChildUserRepository extends JpaRepository<ChildUser, Long> {

    // 아이디로 자녀 계정 찾기
    Optional<ChildUser> findByUserId(String userId);

    // 전화번호로 자녀 계정 찾기 (중복 가입 방지용)
    Optional<ChildUser> findByPhone(String phone);

    // 소셜 로그인 정보로 자녀 계정 찾기
    @Query("SELECT cu FROM ChildUser cu "
         + "JOIN AuthIdentity ai ON ai.childUser = cu "
         + "WHERE ai.provider = :provider "
         + "AND ai.providerUserId = :providerUserId")
    Optional<ChildUser> findBySocialLogin(@Param("provider") SocialLogin provider,
                                          @Param("providerUserId") String providerUserId);
}
