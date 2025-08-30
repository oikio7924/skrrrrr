package com.skrrrrr.harudam.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ChatDailySummaryRepository extends JpaRepository<ChatDailySummary, Long> {

    // 부모-자녀 하루 요약 조회
    Optional<ChatDailySummary> findByParentUser_IdAndChildUser_IdAndSummaryDate(
            Long parentId, Long childId, LocalDate summaryDate
    );

    // 특정 자녀의 월별 요약 조회 (Timeline)
    List<ChatDailySummary> findByChildUser_IdAndSummaryDateBetweenOrderBySummaryDateAsc(
            Long childId, LocalDate start, LocalDate end
    );
}
