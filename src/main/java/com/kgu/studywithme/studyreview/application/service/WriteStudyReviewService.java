package com.kgu.studywithme.studyreview.application.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyreview.application.usecase.command.WriteStudyReviewUseCase;
import com.kgu.studywithme.studyreview.domain.repository.StudyReviewRepository;
import com.kgu.studywithme.studyreview.exception.StudyReviewErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WriteStudyReviewService implements WriteStudyReviewUseCase {
    private final StudyParticipantRepository studyParticipantRepository;
    private final StudyReviewRepository studyReviewRepository;

    @Override
    public Long invoke(final Command command) {
        validateMemberIsGraduatedStudy(command.studyId(), command.memberId());
        validateAlreadyWritten(command.studyId(), command.memberId());

        return studyReviewRepository.save(command.toDomain()).getId();
    }

    private void validateMemberIsGraduatedStudy(final Long studyId, final Long memberId) {
        if (!studyParticipantRepository.isGraduatedParticipant(studyId, memberId)) {
            throw StudyWithMeException.type(StudyReviewErrorCode.ONLY_GRADUATED_PARTICIPANT_CAN_WRITE_REVIEW);
        }
    }

    private void validateAlreadyWritten(final Long studyId, final Long memberId) {
        if (studyReviewRepository.existsByStudyIdAndWriterId(studyId, memberId)) {
            throw StudyWithMeException.type(StudyReviewErrorCode.ALREADY_WRITTEN);
        }
    }
}
