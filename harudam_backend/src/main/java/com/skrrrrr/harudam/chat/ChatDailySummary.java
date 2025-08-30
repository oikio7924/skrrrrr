package com.skrrrrr.harudam.chat;

import com.skrrrrr.harudam.member.ParentUser;
import com.skrrrrr.harudam.common.enums.EmotionWord;
import com.skrrrrr.harudam.member.ChildUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 하루치 대화 요약 (그림일기/Timeline용)
 */
@Entity
@Table(name = "chat_daily_summary",
       uniqueConstraints = @UniqueConstraint(columnNames = {"parent_id","child_id","summaryDate"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatDailySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private ParentUser parentUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private ChildUser childUser;

    @Column(nullable = false)
    private LocalDate summaryDate;

    @Column(columnDefinition = "TEXT")
    private String summaryText;

    @Enumerated(EnumType.STRING)
    private EmotionWord emotion;

    @Column(length = 500)
    private String generatedImageUrl;
}

