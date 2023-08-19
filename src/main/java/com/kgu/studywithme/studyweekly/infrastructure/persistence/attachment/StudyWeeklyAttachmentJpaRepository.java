package com.kgu.studywithme.studyweekly.infrastructure.persistence.attachment;

import com.kgu.studywithme.studyweekly.domain.attachment.StudyWeeklyAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyWeeklyAttachmentJpaRepository extends JpaRepository<StudyWeeklyAttachment, Long> {
}
