package com.skrrrrr.harudam.timeline;

import com.skrrrrr.harudam.chat.ChatDailySummary;
import com.skrrrrr.harudam.chat.ChatDailySummaryRepository;
import com.skrrrrr.harudam.chat.ChatSummaryService;
import com.skrrrrr.harudam.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TimelineController {

    private final ChatSummaryService chatSummaryService;
    private final ChatDailySummaryRepository chatDailySummaryRepository;

    /**
     * ✅ 오늘 하루 요약 + 그림일기 생성 & 반환 (부모 요청 시 실행)
     */
    @PostMapping("/summary/today/{childId}")
    public ResponseEntity<ApiResponse<ChatDailySummary>> summarizeToday(
            @PathVariable Long childId,
            @AuthenticationPrincipal User principal) {

        Long parentId = Long.valueOf(principal.getUsername());
        LocalDate today = LocalDate.now();

        ChatDailySummary summary = chatSummaryService.summarizeDaily(parentId, childId, today);
        return ResponseEntity.ok(ApiResponse.ok("TODAY_SUMMARY", summary));
    }

    /**
     * ✅ 월별 그림일기 조회 (자녀 화면 전용)
     * ex) GET /api/timeline/child/{childId}?year=2025&month=8
     */
    @GetMapping("/child/{childId}")
    public ResponseEntity<ApiResponse<List<ChatDailySummary>>> getMonthlyForChild(
            @PathVariable Long childId,
            @RequestParam int year,
            @RequestParam int month) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<ChatDailySummary> summaries =
                chatDailySummaryRepository.findByChildUser_IdAndSummaryDateBetweenOrderBySummaryDateAsc(
                        childId, start, end);

        return ResponseEntity.ok(ApiResponse.ok("MONTHLY_SUMMARIES_CHILD", summaries));
    }

    /**
     * ✅ 월별 그림일기 조회 (부모 화면 전용)
     * ex) GET /api/timeline/parent/{childId}?year=2025&month=8
     */
    @GetMapping("/parent/{childId}")
    public ResponseEntity<ApiResponse<List<ChatDailySummary>>> getMonthlyForParent(
            @PathVariable Long childId,
            @RequestParam int year,
            @RequestParam int month,
            @AuthenticationPrincipal User principal) {

        Long parentId = Long.valueOf(principal.getUsername());
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<ChatDailySummary> summaries =
                chatDailySummaryRepository.findByParentUser_IdAndChildUser_IdAndSummaryDateBetweenOrderBySummaryDateAsc(
                        parentId, childId, start, end);

        return ResponseEntity.ok(ApiResponse.ok("MONTHLY_SUMMARIES_PARENT", summaries));
    }

    /**
     * ✅ 특정 날짜 그림일기 조회 (자녀 or 부모 모두 가능)
     * ex) GET /api/timeline/{childId}/2025-08-30
     */
    @GetMapping("/{childId}/{date}")
    public ResponseEntity<ApiResponse<ChatDailySummary>> getDiaryByDate(
            @PathVariable Long childId,
            @PathVariable String date,
            @AuthenticationPrincipal User principal) {

        Long parentId = Long.valueOf(principal.getUsername());
        LocalDate targetDate = LocalDate.parse(date);

        Optional<ChatDailySummary> summary =
                chatDailySummaryRepository.findByParentUser_IdAndChildUser_IdAndSummaryDate(
                        parentId, childId, targetDate);

        return summary.map(s -> ResponseEntity.ok(ApiResponse.ok("DIARY_ENTRY", s)))
                .orElse(ResponseEntity.ok(ApiResponse.fail("NO_DIARY_FOR_DATE")));
    }
}
