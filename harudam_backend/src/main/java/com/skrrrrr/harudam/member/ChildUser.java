package com.skrrrrr.harudam.member;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.skrrrrr.harudam.common.enums.Gender;
import com.skrrrrr.harudam.common.enums.OnboardingState;
import com.skrrrrr.harudam.common.enums.UserStatus;
import com.skrrrrr.harudam.common.enums.UserType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
	name = "child_users",
	uniqueConstraints = {
			@UniqueConstraint(name = "ux_child_email", columnNames = "email"),
			@UniqueConstraint(name = "ux_child_user_id", columnNames = "user_id"),
			@UniqueConstraint(name = "ux_child_phone", columnNames = "phone")
	}
)
@Getter
@Setter
public class ChildUser {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = true, length = 50)
	private String nickname;
	
	@Column(name = "email", nullable = true, unique = true, length = 255)
	private String email;
	
	@Column(name = "user_id", nullable = true, length = 100)
	private String userId;
	
	@Column(name = "password_hash", nullable = true, length = 255)
	private String passwordHash;
	
	@Column(length = 50)
	private String name;
	
	@Enumerated(EnumType.STRING)
	private Gender gender;
	
	private LocalDate birth;
	
	@Column(nullable = true, unique = true, length = 20)
	private String phone;
	
	@Column(columnDefinition = "text")
	private String address;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private UserStatus status = UserStatus.PENDING;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = true, length = 20)
	private UserType userType = UserType.CHILD;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = true, length = 20)
	private OnboardingState onboardingState = OnboardingState.NONE;
	
	@CreationTimestamp
	@Column(updatable = false)
	private ZonedDateTime createdAt;
	
	@UpdateTimestamp
	private ZonedDateTime updatedAt;
}