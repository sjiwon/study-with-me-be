package com.kgu.studywithme.member.application.usecase;

import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.member.application.usecase.query.GetAttendanceRatioById;
import com.kgu.studywithme.member.application.usecase.query.GetGraduatedStudyById;
import com.kgu.studywithme.member.application.usecase.query.GetParticipateStudyById;
import com.kgu.studywithme.member.application.usecase.query.GetPublicInformationById;
import com.kgu.studywithme.member.application.usecase.query.GetReceivedReviewById;
import com.kgu.studywithme.member.domain.repository.query.MemberInformationRepository;
import com.kgu.studywithme.member.domain.repository.query.dto.AttendanceRatio;
import com.kgu.studywithme.member.domain.repository.query.dto.GraduatedStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.MemberPublicInformation;
import com.kgu.studywithme.member.domain.repository.query.dto.ParticipateStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.ReceivedReview;
import lombok.RequiredArgsConstructor;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class MemberPublicQueryUseCase {
    private final MemberInformationRepository memberInformationRepository;

    public MemberPublicInformation getPublicInformationById(final GetPublicInformationById query) {
        return memberInformationRepository.fetchPublicInformationById(query.memberId());
    }

    public List<ParticipateStudy> getParticipateStudyById(final GetParticipateStudyById query) {
        return memberInformationRepository.fetchParticipateStudyById(query.memberId());
    }

    public List<GraduatedStudy> getGraduatedStudyById(final GetGraduatedStudyById query) {
        return memberInformationRepository.fetchGraduatedStudyById(query.memberId());
    }

    public List<ReceivedReview> getReceivedReviewById(final GetReceivedReviewById query) {
        return memberInformationRepository.fetchReceivedReviewById(query.memberId());
    }

    public List<AttendanceRatio> getAttendanceRatioById(final GetAttendanceRatioById query) {
        return memberInformationRepository.fetchAttendanceRatioById(query.memberId());
    }
}
