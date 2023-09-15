package com.kgu.studywithme.study.domain.repository;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long> {
    default Study getById(final Long id) {
        return findById(id)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_NOT_FOUND));
    }
}
