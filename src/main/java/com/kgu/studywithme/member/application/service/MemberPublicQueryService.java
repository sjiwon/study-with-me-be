package com.kgu.studywithme.member.application.service;

import com.kgu.studywithme.member.application.usecase.query.QueryAttendanceRatioByIdUseCase;
import com.kgu.studywithme.member.application.usecase.query.QueryGraduatedStudyByIdUseCase;
import com.kgu.studywithme.member.application.usecase.query.QueryParticipateStudyByIdUseCase;
import com.kgu.studywithme.member.application.usecase.query.QueryPublicInformationByIdUseCase;
import com.kgu.studywithme.member.application.usecase.query.QueryReceivedReviewByIdUseCase;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.AttendanceRatio;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.GraduatedStudy;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.MemberPublicInformation;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.ParticipateStudy;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.ReceivedReview;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberPublicQueryService implements
        QueryPublicInformationByIdUseCase,
        QueryParticipateStudyByIdUseCase,
        QueryGraduatedStudyByIdUseCase,
        QueryReceivedReviewByIdUseCase,
        QueryAttendanceRatioByIdUseCase {

    private final MemberRepository memberRepository;

    @Override
    public MemberPublicInformation invoke(final QueryPublicInformationByIdUseCase.Query query) {
        return memberRepository.fetchPublicInformationById(query.memberId());
    }

    @Override
    public List<ParticipateStudy> invoke(final QueryParticipateStudyByIdUseCase.Query query) {
        return memberRepository.fetchParticipateStudyById(query.memberId());
    }

    @Override
    public List<GraduatedStudy> invoke(final QueryGraduatedStudyByIdUseCase.Query query) {
        return memberRepository.fetchGraduatedStudyById(query.memberId());
    }

    @Override
    public List<ReceivedReview> invoke(final QueryReceivedReviewByIdUseCase.Query query) {
        return memberRepository.fetchReceivedReviewById(query.memberId());
    }

    @Override
    public List<AttendanceRatio> invoke(final QueryAttendanceRatioByIdUseCase.Query query) {
        return memberRepository.fetchAttendanceRatioById(query.memberId());
    }
}
