package com.kgu.studywithme.studynotice.infrastructure.persistence;

import com.kgu.studywithme.studynotice.domain.StudyNotice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyNoticeJpaRepository extends JpaRepository<StudyNotice, Long> {
}
