package com.kgu.studywithme.study.domain.repository;

import com.kgu.studywithme.study.domain.model.Study;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long> {
}
