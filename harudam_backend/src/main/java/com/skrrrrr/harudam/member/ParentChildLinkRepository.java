package com.skrrrrr.harudam.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentChildLinkRepository extends JpaRepository<ParentChildLink, Long>{

}