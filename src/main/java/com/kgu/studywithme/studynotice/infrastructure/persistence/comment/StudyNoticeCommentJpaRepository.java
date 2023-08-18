package com.kgu.studywithme.studynotice.infrastructure.persistence.comment;

import com.kgu.studywithme.studynotice.domain.comment.StudyNoticeComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyNoticeCommentJpaRepository extends JpaRepository<StudyNoticeComment, Long> {
}
