package com.kgu.studywithme.peerreview.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.StudyParticipateWeeks;
import com.kgu.studywithme.peerreview.application.usecase.command.WritePeerReviewUseCase;
import com.kgu.studywithme.peerreview.domain.PeerReview;
import com.kgu.studywithme.peerreview.domain.PeerReviewRepository;
import com.kgu.studywithme.peerreview.exception.PeerReviewErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class WritePeerReviewService implements WritePeerReviewUseCase {
    private final PeerReviewRepository peerReviewRepository;
    private final MemberRepository memberRepository;

    @Override
    public Long writePeerReview(final Command command) {
        validateSelfReview(command.reviewerId(), command.revieweeId());
        validateColleague(command.reviewerId(), command.revieweeId());
        validateAlreadyReviewed(command.reviewerId(), command.revieweeId());

        final PeerReview peerReview = PeerReview.doReview(command.reviewerId(), command.revieweeId(), command.content());
        return peerReviewRepository.save(peerReview).getId();
    }

    private void validateSelfReview(
            final Long reviewerId,
            final Long revieweeId
    ) {
        if (reviewerId.equals(revieweeId)) {
            throw StudyWithMeException.type(PeerReviewErrorCode.SELF_REVIEW_NOT_ALLOWED);
        }
    }

    private void validateColleague(
            final Long reviewerId,
            final Long revieweeId
    ) {
        final List<StudyParticipateWeeks> reviewerMetadata
                = memberRepository.findParticipateWeeksInStudyByMemberId(reviewerId);
        final List<StudyParticipateWeeks> revieweeMetadata
                = memberRepository.findParticipateWeeksInStudyByMemberId(revieweeId);

        final boolean hasCommonMetadata =
                reviewerMetadata.stream()
                        .anyMatch(revieweeData ->
                                revieweeMetadata.stream()
                                        .anyMatch(reviewerData -> hasCommonMetadata(reviewerData, revieweeData))
                        );

        if (!hasCommonMetadata) {
            throw StudyWithMeException.type(PeerReviewErrorCode.COMMON_STUDY_RECORD_NOT_FOUND);
        }
    }

    private boolean hasCommonMetadata(
            final StudyParticipateWeeks reviewerData,
            final StudyParticipateWeeks revieweeData
    ) {
        return reviewerData.studyId().equals(revieweeData.studyId())
                && reviewerData.week() == revieweeData.week();
    }

    private void validateAlreadyReviewed(
            final Long reviewerId,
            final Long revieweeId
    ) {
        if (peerReviewRepository.existsByReviewerIdAndRevieweeId(reviewerId, revieweeId)) {
            throw StudyWithMeException.type(PeerReviewErrorCode.ALREADY_REVIEW);
        }
    }
}
