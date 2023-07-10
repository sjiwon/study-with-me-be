package com.kgu.studywithme.study.application;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class StudyFindService {
    private final StudyRepository studyRepository;

    public Study findById(final Long studyId) {
        return studyRepository.findById(studyId)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_NOT_FOUND));
    }

    public Study findByIdWithParticipants(final Long studyId) {
        return studyRepository.findByIdWithParticipants(studyId)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_NOT_FOUND));
    }

    public Study findByIdWithHost(final Long studyId) {
        return studyRepository.findByIdWithHost(studyId)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_NOT_FOUND));
    }

    public Study findByIdAndHostId(
            final Long studyId,
            final Long hostId
    ) {
        return studyRepository.findByIdAndHostId(studyId, hostId)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_NOT_FOUND));
    }

    public Study findByIdAndHostIdWithParticipants(
            final Long studyId,
            final Long hostId
    ) {
        return studyRepository.findByIdAndHostIdWithParticipants(studyId, hostId)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.STUDY_NOT_FOUND));
    }
}
