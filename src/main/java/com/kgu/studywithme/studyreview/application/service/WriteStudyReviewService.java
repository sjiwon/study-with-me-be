package com.kgu.studywithme.studyreview.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import com.kgu.studywithme.studyreview.application.usecase.command.WriteStudyReviewUseCase;
import com.kgu.studywithme.studyreview.domain.StudyReview;
import com.kgu.studywithme.studyreview.domain.StudyReviewRepository;
import com.kgu.studywithme.studyreview.exception.StudyReviewErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class WriteStudyReviewService implements WriteStudyReviewUseCase {
    private final StudyParticipantRepository studyParticipantRepository;
    private final StudyReviewRepository studyReviewRepository;

    @Override
    public Long invoke(final Command command) {
        validateMemberIsGraduatedStudy(command.studyId(), command.memberId());
        validateAlreadyWritten(command.studyId(), command.memberId());

        final StudyReview review = StudyReview.writeReview(
                command.studyId(),
                command.memberId(),
                command.content()
        );
        return studyReviewRepository.save(review).getId();
    }

    private void validateMemberIsGraduatedStudy(
            final Long studyId,
            final Long memberId
    ) {
        if (!studyParticipantRepository.isGraduatedParticipant(studyId, memberId)) {
            throw StudyWithMeException.type(StudyReviewErrorCode.ONLY_GRADUATED_PARTICIPANT_CAN_WRITE_REVIEW);
        }
    }

    private void validateAlreadyWritten(
            final Long studyId,
            final Long memberId
    ) {
        if (studyReviewRepository.existsByStudyIdAndWriterId(studyId, memberId)) {
            throw StudyWithMeException.type(StudyReviewErrorCode.ALREADY_WRITTEN);
        }
    }
}
