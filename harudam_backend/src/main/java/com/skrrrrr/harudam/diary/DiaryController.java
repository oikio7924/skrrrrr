//package com.skrrrr.harudam.diary;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/diaries")
//@RequiredArgsConstructor
//public class DiaryController {
//
//    private final DiaryService diaryService;
//
//    /**
//     * 일기 생성 API
//     */
//    @PostMapping
//    public ResponseEntity<Long> createDiary(@RequestBody DiaryCreateRequestDto requestDto) {
//        // TODO: 추후 인증 로직을 통해 실제 사용자 ID를 가져와야 합니다.
//        Long memberId = 1L; // 임시 사용자 ID
//        Long diaryId = diaryService.createDiary(memberId, requestDto);
//        return ResponseEntity.ok(diaryId);
//    }
//
//    /**
//     * 특정 날짜의 일기 조회 API
//     */
//    @GetMapping("/by-date/{date}")
//    public ResponseEntity<DiaryResponseDto> getDiaryByDate(
//            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//        // TODO: 추후 인증 로직을 통해 실제 사용자 ID를 가져와야 합니다.
//        Long memberId = 1L; // 임시 사용자 ID
//        DiaryResponseDto diary = diaryService.getDiaryByDate(memberId, date);
//        return ResponseEntity.ok(diary);
//    }
//
//    /**
//     * 특정 ID의 일기 조회 API
//     */
//    @GetMapping("/{id}")
//    public ResponseEntity<DiaryResponseDto> getDiaryById(@PathVariable Long id) {
//        DiaryResponseDto diary = diaryService.getDiaryById(id);
//        return ResponseEntity.ok(diary);
//    }
//
//    /**
//     * 특정 사용자의 모든 일기 목록 조회 API
//     */
//    @GetMapping
//    public ResponseEntity<List<DiaryResponseDto>> getAllDiaries() {
//        // TODO: 추후 인증 로직을 통해 실제 사용자 ID를 가져와야 합니다.
//        Long memberId = 1L; // 임시 사용자 ID
//        List<DiaryResponseDto> diaries = diaryService.getAllDiariesByMemberId(memberId);
//        return ResponseEntity.ok(diaries);
//    }
//
//    /**
//     * 일기 수정 API
//     */
//    @PutMapping("/{id}")
//    public ResponseEntity<Long> updateDiary(@PathVariable Long id, @RequestBody DiaryUpdateRequestDto requestDto) {
//        Long updatedDiaryId = diaryService.updateDiary(id, requestDto);
//        return ResponseEntity.ok(updatedDiaryId);
//    }
//
//    /**
//     * 일기 삭제 API
//     */
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteDiary(@PathVariable Long id) {
//        diaryService.deleteDiary(id);
//        return ResponseEntity.ok("Diary deleted successfully.");
//    }
//}