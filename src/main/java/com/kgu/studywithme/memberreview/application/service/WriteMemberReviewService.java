package com.kgu.studywithme.memberreview.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.adapter.MemberAttendanceRepositoryAdapter;
import com.kgu.studywithme.member.infrastructure.query.dto.StudyParticipateWeeks;
import com.kgu.studywithme.memberreview.application.usecase.command.WriteMemberReviewUseCase;
import com.kgu.studywithme.memberreview.domain.MemberReview;
import com.kgu.studywithme.memberreview.domain.MemberReviewRepository;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class WriteMemberReviewService implements WriteMemberReviewUseCase {
    private final MemberAttendanceRepositoryAdapter memberAttendanceRepositoryAdapter;
    private final MemberReviewRepository memberReviewRepository;

    @Override
    public Long invoke(final Command command) {
        validateSelfReview(command.reviewerId(), command.revieweeId());
        validateColleague(command.reviewerId(), command.revieweeId());
        validateAlreadyReviewed(command.reviewerId(), command.revieweeId());

        final MemberReview memberReview = MemberReview.doReview(command.reviewerId(), command.revieweeId(), command.content());
        return memberReviewRepository.save(memberReview).getId();
    }

    private void validateSelfReview(
            final Long reviewerId,
            final Long revieweeId
    ) {
        if (reviewerId.equals(revieweeId)) {
            throw StudyWithMeException.type(MemberReviewErrorCode.SELF_REVIEW_NOT_ALLOWED);
        }
    }

    private void validateColleague(
            final Long reviewerId,
            final Long revieweeId
    ) {
        final List<StudyParticipateWeeks> reviewerMetadata
                = memberAttendanceRepositoryAdapter.findParticipateWeeksInStudyByMemberId(reviewerId);
        final List<StudyParticipateWeeks> revieweeMetadata
                = memberAttendanceRepositoryAdapter.findParticipateWeeksInStudyByMemberId(revieweeId);

        final boolean hasCommonMetadata =
                reviewerMetadata.stream()
                        .anyMatch(revieweeData ->
                                revieweeMetadata.stream()
                                        .anyMatch(reviewerData -> hasCommonMetadata(reviewerData, revieweeData))
                        );

        if (!hasCommonMetadata) {
            throw StudyWithMeException.type(MemberReviewErrorCode.COMMON_STUDY_RECORD_NOT_FOUND);
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
        if (memberReviewRepository.existsByReviewerIdAndRevieweeId(reviewerId, revieweeId)) {
            throw StudyWithMeException.type(MemberReviewErrorCode.ALREADY_REVIEW);
        }
    }
}
