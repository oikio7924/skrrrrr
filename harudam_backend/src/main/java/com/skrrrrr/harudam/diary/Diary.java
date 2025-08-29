//package com.skrrrr.harudam.diary;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Entity
//@Getter
//@NoArgsConstructor
//public class Diary {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String title;
//
//    @Column(columnDefinition = "TEXT", nullable = false)
//    private String content;
//
//    @Column
//    private String imageUrl;
//
//    @Column(unique = true, nullable = false)
//    private LocalDate diaryDate;
//
//    @Column(updatable = false)
//    private LocalDateTime createdAt;
//    
//    private LocalDateTime updatedAt;
//
//    // TODO: Member 엔티티와 연관관계 설정
//    // @ManyToOne(fetch = FetchType.LAZY)
//    // @JoinColumn(name = "member_id")
//    // private Member member;
//
//    public Diary(String title, String content, String imageUrl, LocalDate diaryDate) {
//        this.title = title;
//        this.content = content;
//        this.imageUrl = imageUrl;
//        this.diaryDate = diaryDate;
//    }
//
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//        updatedAt = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }
//
//    public void update(String title, String content, String imageUrl) {
//        this.title = title;
//        this.content = content;
//        this.imageUrl = imageUrl;
//    }
//}