package com.kgu.studywithme.member.application;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.domain.review.PeerReview;
import com.kgu.studywithme.member.domain.review.PeerReviewRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.StudyAttendanceMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberReviewService {
    private final MemberFindService memberFindService;
    private final MemberRepository memberRepository;
    private final PeerReviewRepository peerReviewRepository;

    @Transactional
    public void writeReview(
            final Long revieweeId,
            final Long reviewerId,
            final String content
    ) {
        validateSelfReviewNotAllowed(revieweeId, reviewerId);
        validateColleague(revieweeId, reviewerId);

        final Member reviewee = memberFindService.findById(revieweeId);
        final Member reviewer = memberFindService.findById(reviewerId);

        reviewee.applyPeerReview(reviewer, content);
    }

    private void validateSelfReviewNotAllowed(
            final Long revieweeId,
            final Long reviewerId
    ) {
        if (revieweeId.equals(reviewerId)) {
            throw StudyWithMeException.type(MemberErrorCode.SELF_REVIEW_NOT_ALLOWED);
        }
    }

    private void validateColleague(
            final Long revieweeId,
            final Long reviewerId
    ) {
        final List<StudyAttendanceMetadata> revieweeMetadata
                = memberRepository.findStudyAttendanceMetadataByMemberId(revieweeId);
        final List<StudyAttendanceMetadata> reviewerMetadata
                = memberRepository.findStudyAttendanceMetadataByMemberId(reviewerId);

        final boolean hasCommonMetadata =
                revieweeMetadata
                        .stream()
                        .anyMatch(revieweeData ->
                                reviewerMetadata.stream()
                                        .anyMatch(reviewerData -> hasCommonMetadata(revieweeData, reviewerData))
                        );

        if (!hasCommonMetadata) {
            throw StudyWithMeException.type(MemberErrorCode.COMMON_STUDY_RECORD_NOT_FOUND);
        }
    }

    private boolean hasCommonMetadata(
            final StudyAttendanceMetadata revieweeData,
            final StudyAttendanceMetadata reviewerData
    ) {
        return revieweeData.studyId().equals(reviewerData.studyId()) && revieweeData.week() == reviewerData.week();
    }

    @Transactional
    public void updateReview(
            final Long revieweeId,
            final Long reviewerId,
            final String content
    ) {
        PeerReview peerReview = peerReviewRepository.findByRevieweeIdAndReviewerId(revieweeId, reviewerId)
                .orElseThrow(() -> StudyWithMeException.type(MemberErrorCode.PEER_REVIEW_NOT_FOUND));
        peerReview.updateReview(content);
    }
}
