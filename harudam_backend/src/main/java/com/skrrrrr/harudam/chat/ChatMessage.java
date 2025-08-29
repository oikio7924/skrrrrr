package com.skrrrrr.harudam.chat;

import com.skrrrrr.harudam.member.ParentUser;
import com.skrrrrr.harudam.common.enums.SenderType;
import com.skrrrrr.harudam.member.ChildUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;

/**
 * 부모 ↔ AI 페르소나 대화 원본 로그
 */
@Entity
@Table(name = "chat_message")
@Getter @Setter
@NoArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private ParentUser parentUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private ChildUser childUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SenderType senderType;  // USER(부모) / BOT(AI)

    @Column(nullable = false, length = 2000)
    private String content;         // 텍스트 내용

    private String voiceFilePath;   // 부모 발화 원본 음성 파일 경로
    private String ttsFilePath;     // AI 응답 음성 파일 경로

    @CreationTimestamp
    @Column(updatable = false)
    private ZonedDateTime createdAt;
}
