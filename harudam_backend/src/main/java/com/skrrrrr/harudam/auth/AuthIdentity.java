package com.skrrrrr.harudam.auth;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.skrrrrr.harudam.common.enums.SocialLogin;
import com.skrrrrr.harudam.member.ChildUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "auth_identities", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"provider", "provider_user_id"}, name = "ux_ai_provider_user")
})
@Getter
@Setter
public class AuthIdentity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long identityId;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SocialLogin provider;
	
	@Column(nullable = false, length = 128)
	private String providerUserId;
	
	@Column(length = 255)
	private String providerEmail;
	
	@Column(length = 100)
	private String displayName;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private ChildUser childUser;
	
	@Column(columnDefinition = "text")
	private String accessTokenEnc;
	
	@Column(columnDefinition = "text")
	private String refreshTokenEnc;
	
	private ZonedDateTime expiresAt;
	
	@CreationTimestamp
	@Column(updatable = false)
	private ZonedDateTime connectedAt;
	
	private ZonedDateTime lastLoginAt;
	

}