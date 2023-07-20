package com.kgu.studywithme.studyweekly.domain;

import com.kgu.studywithme.studyweekly.infrastructure.repository.query.StudyWeeklyHandlingRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyWeeklyRepository extends
        JpaRepository<StudyWeekly, Long>,
        StudyWeeklyHandlingRepository {
}
