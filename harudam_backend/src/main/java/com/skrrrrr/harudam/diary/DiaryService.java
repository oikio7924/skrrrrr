//package com.skrrrr.harudam.diary;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class DiaryService {
//
//    private final DiaryRepository diaryRepository;
//    // TODO: 사용자 정보를 가져오기 위해 MemberRepository 주입이 필요합니다.
//    // private final MemberRepository memberRepository;
//
//    @Transactional
//    public Long createDiary(Long memberId, DiaryCreateRequestDto requestDto) {
//        // TODO: memberId로 Member 엔티티 조회 로직 추가
//        Diary diary = new Diary(
//                requestDto.getTitle(),
//                requestDto.getContent(),
//                requestDto.getImageUrl(),
//                requestDto.getDiaryDate()
//        );
//        // diary.setMember(member); // 연관관계 설정
//        return diaryRepository.save(diary).getId();
//    }
//
//    @Transactional(readOnly = true)
//    public DiaryResponseDto getDiaryByDate(Long memberId, LocalDate date) {
//        // TODO: memberId와 date로 Diary 조회 로직으로 변경
//        Diary diary = diaryRepository.findByDiaryDate(date)
//                .orElseThrow(() -> new IllegalArgumentException("Diary not found for date: " + date));
//        return new DiaryResponseDto(diary);
//    }
//
//    @Transactional(readOnly = true)
//    public DiaryResponseDto getDiaryById(Long id) {
//        Diary diary = diaryRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Diary not found with id: " + id));
//        return new DiaryResponseDto(diary);
//    }
//
//    @Transactional(readOnly = true)
//    public List<DiaryResponseDto> getAllDiariesByMemberId(Long memberId) {
//        // TODO: memberId 기준으로 조회하도록 변경
//        return diaryRepository.findAll().stream()
//                .map(DiaryResponseDto::new)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional
//    public Long updateDiary(Long id, DiaryUpdateRequestDto requestDto) {
//        Diary diary = diaryRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Diary not found with id: " + id));
//        // TODO: 수정 권한 확인 로직 추가
//        diary.update(requestDto.getTitle(), requestDto.getContent(), requestDto.getImageUrl());
//        return diary.getId();
//    }
//
//    @Transactional
//    public void deleteDiary(Long id) {
//        // TODO: 삭제 권한 확인 로직 추가
//        diaryRepository.deleteById(id);
//    }
//}