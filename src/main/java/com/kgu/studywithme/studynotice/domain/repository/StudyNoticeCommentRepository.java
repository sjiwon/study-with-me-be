package com.kgu.studywithme.studynotice.domain.repository;

import com.kgu.studywithme.studynotice.domain.model.StudyNoticeComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyNoticeCommentRepository extends JpaRepository<StudyNoticeComment, Long> {
}
