package com.skrrrrr.harudam.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParentChildLinkRepository extends JpaRepository<ParentChildLink, Long> {

    /**
     * ✅ 자녀 ID 기준으로 가장 최근 ParentChildLink 하나 조회
     */
    Optional<ParentChildLink> findFirstByChildUser_IdOrderByCreatedAtDesc(Long childId);

    /**
     * ✅ 특정 부모-자녀 관계 존재 여부 확인
     */
    boolean existsByParentUser_IdAndChildUser_Id(Long parentId, Long childId);
    
    
}
