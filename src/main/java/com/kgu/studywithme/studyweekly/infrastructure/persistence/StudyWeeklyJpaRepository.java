package com.kgu.studywithme.studyweekly.infrastructure.persistence;

import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyWeeklyJpaRepository extends JpaRepository<StudyWeekly, Long> {
}
