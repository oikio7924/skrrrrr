package com.skrrrrr.harudam.auth;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.skrrrrr.harudam.common.enums.AuthCodeStatus;
import com.skrrrrr.harudam.member.ChildUser;
import com.skrrrrr.harudam.member.ParentUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "auth_code")
@Getter
@Setter
public class AuthCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codeId;

    @Column(nullable = false, unique = true, length = 6)
    private String codeValue;

    // ✅ childId 없이도 저장할 수 있도록 nullable = true 로 변경
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by_child_id", nullable = true)
    private ChildUser issuedByChild;

    @Column(nullable = false, length = 20)
    private String targetParentPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthCodeStatus status = AuthCodeStatus.ISSUED;

    @CreationTimestamp
    @Column(updatable = false)
    private ZonedDateTime createdAt;

    @Column(nullable = false)
    private ZonedDateTime expiresAt;

    private ZonedDateTime usedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "used_by_parent_id")
    private ParentUser usedByParent;
}
