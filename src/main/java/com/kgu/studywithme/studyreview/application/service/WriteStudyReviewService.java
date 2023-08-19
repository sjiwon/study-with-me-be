package com.kgu.studywithme.studyreview.application.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyparticipant.application.adapter.ParticipantVerificationRepositoryAdapter;
import com.kgu.studywithme.studyreview.application.usecase.command.WriteStudyReviewUseCase;
import com.kgu.studywithme.studyreview.domain.StudyReview;
import com.kgu.studywithme.studyreview.exception.StudyReviewErrorCode;
import com.kgu.studywithme.studyreview.infrastructure.persistence.StudyReviewJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WriteStudyReviewService implements WriteStudyReviewUseCase {
    private final ParticipantVerificationRepositoryAdapter participantVerificationRepositoryAdapter;
    private final StudyReviewJpaRepository studyReviewJpaRepository;

    @Override
    public Long invoke(final Command command) {
        validateMemberIsGraduatedStudy(command.studyId(), command.memberId());
        validateAlreadyWritten(command.studyId(), command.memberId());

        final StudyReview review = StudyReview.writeReview(
                command.studyId(),
                command.memberId(),
                command.content()
        );
        return studyReviewJpaRepository.save(review).getId();
    }

    private void validateMemberIsGraduatedStudy(
            final Long studyId,
            final Long memberId
    ) {
        if (!participantVerificationRepositoryAdapter.isGraduatedParticipant(studyId, memberId)) {
            throw StudyWithMeException.type(StudyReviewErrorCode.ONLY_GRADUATED_PARTICIPANT_CAN_WRITE_REVIEW);
        }
    }

    private void validateAlreadyWritten(
            final Long studyId,
            final Long memberId
    ) {
        if (studyReviewJpaRepository.existsByStudyIdAndWriterId(studyId, memberId)) {
            throw StudyWithMeException.type(StudyReviewErrorCode.ALREADY_WRITTEN);
        }
    }
}
