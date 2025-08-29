package com.skrrrrr.harudam.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skrrrrr.harudam.common.enums.SocialLogin;

@Repository
public interface AuthIdentityRepository extends JpaRepository<AuthIdentity, Long>{
		Optional<AuthIdentity> findByProviderAndProviderUserId(SocialLogin provider, String providerUserId);
}
