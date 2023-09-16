package com.kgu.studywithme.member.domain.repository.query;

import com.kgu.studywithme.member.domain.repository.query.dto.AppliedStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.AttendanceRatio;
import com.kgu.studywithme.member.domain.repository.query.dto.GraduatedStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.LikeMarkedStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.MemberPrivateInformation;
import com.kgu.studywithme.member.domain.repository.query.dto.MemberPublicInformation;
import com.kgu.studywithme.member.domain.repository.query.dto.ParticipateStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.ReceivedReview;

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
