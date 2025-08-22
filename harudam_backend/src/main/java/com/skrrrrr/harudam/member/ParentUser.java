package com.skrrrrr.harudam.member;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.skrrrrr.harudam.common.enums.Gender;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="parent_users")
@Getter
@Setter
public class ParentUser {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 50)
	private String name;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Gender gender;
	
	@Column(nullable = false)
	private LocalDate birth;
	
	@Column(nullable = false, unique = true, length = 20)
	private String phone;
	
	@Column(columnDefinition = "text")
	private String picture;
	
	@Column(columnDefinition = "text")
	private String address;
	
	private ZonedDateTime lastConversationAt;
	
	@CreationTimestamp
	@Column(updatable = false)
	private ZonedDateTime createdAt;
	
	@UpdateTimestamp
	private ZonedDateTime updatedAt;
	

}