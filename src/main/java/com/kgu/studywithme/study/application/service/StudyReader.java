package com.kgu.studywithme.study.application.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyReader {
    private final StudyRepository studyRepository;

    public Study getById(final Long id) {
        return studyRepository.findById(id)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_NOT_FOUND));
    }
}
