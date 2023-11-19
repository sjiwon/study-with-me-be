package com.kgu.studywithme.studynotice.domain.repository;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studynotice.domain.model.StudyNotice;
import com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyNoticeRepository extends JpaRepository<StudyNotice, Long> {
    default StudyNotice getById(final Long id) {
        return findById(id)
                .orElseThrow(() -> StudyWithMeException.type(StudyNoticeErrorCode.NOTICE_NOT_FOUND));
    }
}
