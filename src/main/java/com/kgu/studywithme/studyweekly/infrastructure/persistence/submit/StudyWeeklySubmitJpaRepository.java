package com.kgu.studywithme.studyweekly.infrastructure.persistence.submit;

import com.kgu.studywithme.studyweekly.domain.submit.StudyWeeklySubmit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyWeeklySubmitJpaRepository extends JpaRepository<StudyWeeklySubmit, Long> {
}
