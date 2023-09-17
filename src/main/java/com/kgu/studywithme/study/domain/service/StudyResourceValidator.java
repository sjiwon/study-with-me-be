package com.kgu.studywithme.study.domain.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.domain.model.StudyName;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyResourceValidator {
    private final StudyRepository studyRepository;

    public void validateInCreate(final StudyName name) {
        if (studyRepository.existsByNameValue(name.getValue())) {
            throw StudyWithMeException.type(StudyErrorCode.DUPLICATE_NAME);
        }
    }

    public void validateInUpdate(final Long studyId, final StudyName name) {
        final Long nameUsedId = studyRepository.findIdByNameUsed(name.getValue());

        if (nameUsedId != null && !nameUsedId.equals(studyId)) {
            throw StudyWithMeException.type(StudyErrorCode.DUPLICATE_NAME);
        }
    }
}
