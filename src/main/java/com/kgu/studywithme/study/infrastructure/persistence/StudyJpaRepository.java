package com.kgu.studywithme.study.infrastructure.persistence;

import com.kgu.studywithme.study.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyJpaRepository extends JpaRepository<Study, Long> {
}
