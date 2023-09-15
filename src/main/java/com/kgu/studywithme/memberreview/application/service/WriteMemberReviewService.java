package com.kgu.studywithme.memberreview.application.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.memberreview.application.usecase.command.WriteMemberReviewUseCase;
import com.kgu.studywithme.memberreview.domain.model.MemberReview;
import com.kgu.studywithme.memberreview.domain.repository.MemberReviewRepository;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
import com.kgu.studywithme.studyattendance.application.adapter.StudyAttendanceHandlingRepositoryAdapter;
import com.kgu.studywithme.studyattendance.infrastructure.query.dto.StudyAttendanceWeekly;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WriteMemberReviewService implements WriteMemberReviewUseCase {
    private final StudyAttendanceHandlingRepositoryAdapter studyAttendanceHandlingRepositoryAdapter;
    private final MemberReviewRepository memberReviewRepository;

    @Override
    public Long invoke(final Command command) {
        validateSelfReview(command.reviewerId(), command.revieweeId());
        validateColleague(command.reviewerId(), command.revieweeId());
        validateAlreadyReviewed(command.reviewerId(), command.revieweeId());

        final MemberReview memberReview = MemberReview.doReview(command.reviewerId(), command.revieweeId(), command.content());
        return memberReviewRepository.save(memberReview).getId();
    }

    private void validateSelfReview(final Long reviewerId, final Long revieweeId) {
        if (reviewerId.equals(revieweeId)) {
            throw StudyWithMeException.type(MemberReviewErrorCode.SELF_REVIEW_NOT_ALLOWED);
        }
    }

    private void validateColleague(final Long reviewerId, final Long revieweeId) {
        final List<StudyAttendanceWeekly> reviewerMetadata
                = studyAttendanceHandlingRepositoryAdapter.findParticipateWeeksInStudyByMemberId(reviewerId);
        final List<StudyAttendanceWeekly> revieweeMetadata
                = studyAttendanceHandlingRepositoryAdapter.findParticipateWeeksInStudyByMemberId(revieweeId);

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

    private boolean hasCommonMetadata(final StudyAttendanceWeekly reviewerData, final StudyAttendanceWeekly revieweeData) {
        return reviewerData.studyId().equals(revieweeData.studyId())
                && reviewerData.week() == revieweeData.week();
    }

    private void validateAlreadyReviewed(final Long reviewerId, final Long revieweeId) {
        if (memberReviewRepository.existsByReviewerIdAndRevieweeId(reviewerId, revieweeId)) {
            throw StudyWithMeException.type(MemberReviewErrorCode.ALREADY_REVIEW);
        }
    }
}
