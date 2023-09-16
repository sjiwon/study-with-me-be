package com.kgu.studywithme.memberreview.domain.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.memberreview.domain.repository.MemberReviewRepository;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
import com.kgu.studywithme.studyattendance.application.adapter.StudyAttendanceHandlingRepositoryAdapter;
import com.kgu.studywithme.studyattendance.infrastructure.query.dto.StudyAttendanceWeekly;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberReviewValidator {
    private final StudyAttendanceHandlingRepositoryAdapter studyAttendanceHandlingRepositoryAdapter;
    private final MemberReviewRepository memberReviewRepository;

    public void validateReviewEligibility(final Long reviewerId, final Long revieweeId) {
        validateSelfReview(reviewerId, revieweeId);
        validateColleague(reviewerId, revieweeId);
        validateAlreadyReviewed(reviewerId, revieweeId);
    }

    private void validateSelfReview(final Long reviewerId, final Long revieweeId) {
        if (reviewerId.equals(revieweeId)) {
            throw StudyWithMeException.type(MemberReviewErrorCode.SELF_REVIEW_NOT_ALLOWED);
        }
    }

    private void validateColleague(final Long reviewerId, final Long revieweeId) {
        final List<StudyAttendanceWeekly> reviewerParticipateData
                = studyAttendanceHandlingRepositoryAdapter.findParticipateWeeksInStudyByMemberId(reviewerId);
        final List<StudyAttendanceWeekly> revieweeParticipateData
                = studyAttendanceHandlingRepositoryAdapter.findParticipateWeeksInStudyByMemberId(revieweeId);

        final boolean hasCommonMetadata = reviewerParticipateData.stream()
                .anyMatch(reviewerData ->
                        revieweeParticipateData.stream()
                                .anyMatch(revieweeData -> hasCommonMetadata(reviewerData, revieweeData))
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
