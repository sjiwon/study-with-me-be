package com.kgu.studywithme.studyweekly.domain.repository;

import com.kgu.studywithme.studyweekly.domain.model.StudyWeeklyAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyWeeklyAttachmentRepository extends JpaRepository<StudyWeeklyAttachment, Long> {
}
