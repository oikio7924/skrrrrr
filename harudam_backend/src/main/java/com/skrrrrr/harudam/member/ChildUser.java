package com.skrrrrr.harudam.member;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.skrrrrr.harudam.common.enums.Gender;
import com.skrrrrr.harudam.common.enums.UserState;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "child_users")
@Getter
@Setter
public class ChildUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 100)
    private String userId;   // 소셜 로그인 ID (카카오, 구글, 네이버)

    @Column(length = 255)
    private String password;

    @Column(length = 50)
    private String name;

    private LocalDate birth;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(unique = true, length = 20)
    private String phone;

    // 원본 파일 경로
    @Column(length = 500)
    private String pictureUrl; // 사진 파일 URL
    
    @Column(length = 500)
    private String voiceUrl;   // 음성 파일 URL
    
    // 주소
    @Column(length = 255)
    private String addr1;  // 기본 주소 (도로명/지번)
    @Column(length = 255)
    private String addr2;  // 상세 주소

    // ✅ AI 학습 결과 경로 추가
    @Column(length = 500)
    private String aiPicturePath;

    @Column(length = 500)
    private String aiVoicePath;

    @OneToMany(mappedBy = "childUser")
    private List<ParentChildLink> parentLinks;
    
    public ParentUser getParentUser() {
    	return parentLinks.isEmpty() ? null : parentLinks.get(0).getParentUser();
    }
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserState state = UserState.PENDING;

    @CreationTimestamp
    @Column(updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
