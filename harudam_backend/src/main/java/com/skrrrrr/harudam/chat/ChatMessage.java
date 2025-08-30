package com.skrrrrr.harudam.chat;

import com.skrrrrr.harudam.member.ParentUser;
import com.skrrrrr.harudam.member.ChildUser;
import com.skrrrrr.harudam.common.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name = "chat_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    public enum SenderType { USER, BOT }

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
    private SenderType senderType;

    @Column(nullable = false, length = 2000)
    private String content;

    private String voiceFilePath;
    private String ttsFilePath;

    @CreationTimestamp
    @Column(updatable = false)
    private ZonedDateTime createdAt;
}
