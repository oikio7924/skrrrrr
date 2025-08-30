package com.skrrrrr.harudam.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 특정 부모-자녀의 하루치 메시지 (날짜 기준 조회)
    List<ChatMessage> findByParentUser_IdAndChildUser_IdAndCreatedAtBetweenOrderByCreatedAtAsc(
            Long parentId, Long childId, LocalDateTime start, LocalDateTime end
    );

    // 페이징 지원 버전 (메시지 히스토리 불러올 때)
    Page<ChatMessage> findByParentUser_IdAndChildUser_IdOrderByCreatedAtDesc(
            Long parentId, Long childId, Pageable pageable
    );

    List<ChatMessage> findByParentUser_IdAndChildUser_IdOrderByCreatedAtAsc(
        Long parentId, Long childId);
}
