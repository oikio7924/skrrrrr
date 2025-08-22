package com.skrrrrr.harudam.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentUserRepository extends JpaRepository<ParentUser, Long>{
	// 전화번호로 부모님 계정을 찾는 기능
	Optional<ParentUser> findByPhone(String phone);
}