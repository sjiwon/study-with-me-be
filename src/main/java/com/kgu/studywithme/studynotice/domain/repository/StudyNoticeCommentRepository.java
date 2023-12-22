package com.kgu.studywithme.studynotice.domain.repository;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studynotice.domain.model.StudyNoticeComment;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyNoticeCommentRepository extends JpaRepository<StudyNoticeComment, Long> {
    @Query("""
            SELECT snc
            FROM StudyNoticeComment snc
            JOIN FETCH snc.writer
            WHERE snc.id = :id
            """)
    Optional<StudyNoticeComment> findByIdWithWriter(@Param("id") final Long id);

    default StudyNoticeComment getByIdWithWriter(final Long id) {
        return findByIdWithWriter(id)
                .orElseThrow(() -> StudyWithMeException.type(StudyNoticeErrorCode.NOTICE_COMMENT_NOT_FOUND));
    }

    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM StudyNoticeComment snc WHERE snc.notice.id = :noticeId")
    void deleteByNoticeId(@Param("noticeId") final Long noticeId);
}
