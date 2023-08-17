package com.kgu.studywithme.studynotice.domain;

import com.kgu.studywithme.studynotice.infrastructure.query.StudyNoticeHandlingRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyNoticeRepository extends
        JpaRepository<StudyNotice, Long>,
        StudyNoticeHandlingRepository {
}
