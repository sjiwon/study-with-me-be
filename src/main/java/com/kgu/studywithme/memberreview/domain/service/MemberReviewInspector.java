package com.kgu.studywithme.memberreview.domain.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.memberreview.domain.repository.MemberReviewRepository;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
import com.kgu.studywithme.studyattendance.domain.repository.query.StudyAttendanceMetadataRepository;
import com.kgu.studywithme.studyattendance.domain.repository.query.dto.StudyAttendanceWeekly;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberReviewInspector {
    private final StudyAttendanceMetadataRepository studyAttendanceMetadataRepository;
    private final MemberReviewRepository memberReviewRepository;

    public void checkReviewerHasEligibilityToReview(final Member reviewer, final Member reviewee) {
        validateSelfReview(reviewer, reviewee);
        validateColleague(reviewer, reviewee);
        validateAlreadyReviewed(reviewer, reviewee);
    }

    private void validateSelfReview(final Member reviewer, final Member reviewee) {
        if (reviewer.isSameMember(reviewee)) {
            throw StudyWithMeException.type(MemberReviewErrorCode.SELF_REVIEW_NOT_ALLOWED);
        }
    }

    private void validateColleague(final Member reviewer, final Member reviewee) {
        final List<StudyAttendanceWeekly> reviewerAttendanceData = studyAttendanceMetadataRepository.findMemberParticipateWeekly(reviewer.getId());
        final List<StudyAttendanceWeekly> revieweeAttendanceData = studyAttendanceMetadataRepository.findMemberParticipateWeekly(reviewee.getId());

        final boolean hasCommonMetadata = reviewerAttendanceData.stream()
                .anyMatch(reviewerData ->
                        revieweeAttendanceData.stream()
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

    private void validateAlreadyReviewed(final Member reviewer, final Member reviewee) {
        if (memberReviewRepository.existsByReviewerIdAndRevieweeId(reviewer.getId(), reviewee.getId())) {
            throw StudyWithMeException.type(MemberReviewErrorCode.ALREADY_REVIEW);
        }
    }
}
