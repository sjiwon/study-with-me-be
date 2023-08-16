package com.kgu.studywithme.member.application.adapter;

import com.kgu.studywithme.member.infrastructure.query.dto.AppliedStudy;
import com.kgu.studywithme.member.infrastructure.query.dto.AttendanceRatio;
import com.kgu.studywithme.member.infrastructure.query.dto.GraduatedStudy;
import com.kgu.studywithme.member.infrastructure.query.dto.LikeMarkedStudy;
import com.kgu.studywithme.member.infrastructure.query.dto.MemberPrivateInformation;
import com.kgu.studywithme.member.infrastructure.query.dto.MemberPublicInformation;
import com.kgu.studywithme.member.infrastructure.query.dto.ParticipateStudy;
import com.kgu.studywithme.member.infrastructure.query.dto.ReceivedReview;

import java.util.List;

public interface MemberInformationRepositoryAdapter {
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
