package com.kgu.studywithme.member.infrastructure.repository.query;

import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.*;

import java.util.List;

public interface MemberInformationRepository {
    // Public Information
    MemberPublicInformation fetchPublicInformationById(final Long memberId);

    List<ParticipateStudy> fetchParticipateStudyById(final Long memberId);

    List<GraduatedStudy> fetchGraduatedStudyById(final Long memberId);

    List<ReceivedReview> fetchReceivedReviewById(final Long memberId);

    List<AttendanceRatio> fetchAttendanceRatioById(final Long memberId);

    // Private Information
    MemberPrivateInformation fetchPrivateInformationById(final Long memberId);

    List<AppliedStudy> fetchAppliedStudyById(final Long memberId);

    List<LikeMarkedStudy> fetchLikeMarkedStudyById(final Long memberId);
}
