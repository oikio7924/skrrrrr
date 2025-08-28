package com.skrrrrr.harudam.member;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.skrrrrr.harudam.common.enums.Gender;
import com.skrrrrr.harudam.common.enums.UserState;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "parent_users")
@Getter
@Setter
public class ParentUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소셜 로그인 ID 또는 자체 아이디
    @Column(unique = true, length = 100)
    private String userId;

    @Column(length = 50)
    private String name;

    @Column(unique = true, length = 20)
    private String phone;
    
    private LocalDate birth;
    
    // 주소
    @Column(length = 255)
    private String addr1;  // 기본 주소
    @Column(length = 255)
    private String addr2;  // 상세 주소

    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    // 원본 파일 경로
    @Column(length = 500)
    private String pictureUrl; // 사진 파일 URL

    @Column(length = 500)
    private String voiceUrl;   // 음성 파일 URL

    // ✅ AI 학습 결과 파일 경로
    @Column(length = 500)
    private String aiPicturePath;

    @Column(length = 500)
    private String aiVoicePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserState state = UserState.PENDING;

    @CreationTimestamp
    @Column(updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
