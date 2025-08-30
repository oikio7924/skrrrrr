package com.skrrrrr.harudam.timeline;

import com.skrrrrr.harudam.chat.ChatDailySummary;
import com.skrrrrr.harudam.chat.ChatSummaryService;
import com.skrrrrr.harudam.chat.ChatDailySummaryRepository;
import com.skrrrrr.harudam.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TimelineController {

    private final ChatSummaryService chatSummaryService;
    private final ChatDailySummaryRepository chatDailySummaryRepository;

    /**
     * ✅ 오늘 요약 생성 & 조회
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
     * ✅ 월별 데이터 조회
     */
    @GetMapping("/{childId}")
    public ResponseEntity<ApiResponse<List<ChatDailySummary>>> getMonthly(
            @PathVariable Long childId,
            @RequestParam int year,
            @RequestParam int month,
            @AuthenticationPrincipal User principal) {

        Long parentId = Long.valueOf(principal.getUsername());
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<ChatDailySummary> list = chatDailySummaryRepository
                .findByChildUser_IdAndSummaryDateBetweenOrderBySummaryDateAsc(childId, start, end);

        return ResponseEntity.ok(ApiResponse.ok("MONTHLY_SUMMARIES", list));
    }
}
