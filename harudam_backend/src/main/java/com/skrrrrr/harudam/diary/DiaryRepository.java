//package com.skrrrr.harudam.diary;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.Optional;
//
//@Repository
//public interface DiaryRepository extends JpaRepository<Diary, Long> {
//    
//    Optional<Diary> findByDiaryDate(LocalDate diaryDate);
//    
//    // TODO: 추후 사용자 인증 기능 추가 후 아래 메소드 사용
//    // Optional<Diary> findByMemberIdAndDiaryDate(Long memberId, LocalDate diaryDate);
//    // List<Diary> findAllByMemberId(Long memberId);
//}