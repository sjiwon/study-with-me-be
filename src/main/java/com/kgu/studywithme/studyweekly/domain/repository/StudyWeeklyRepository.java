package com.kgu.studywithme.studyweekly.domain.repository;

import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyWeeklyRepository extends JpaRepository<StudyWeekly, Long> {
}
