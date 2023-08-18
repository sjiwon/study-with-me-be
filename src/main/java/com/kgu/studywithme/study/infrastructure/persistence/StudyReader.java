package com.kgu.studywithme.study.infrastructure.persistence;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.application.adapter.StudyReadAdapter;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyReader implements StudyReadAdapter {
    private final StudyJpaRepository studyJpaRepository;

    @Override
    public Study getById(final Long id) {
        return studyJpaRepository.findById(id)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_NOT_FOUND));
    }
}
