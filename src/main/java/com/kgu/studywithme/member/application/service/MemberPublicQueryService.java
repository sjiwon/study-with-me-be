package com.kgu.studywithme.member.application.service;

import com.kgu.studywithme.member.application.usecase.query.*;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.*;
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
    public MemberPublicInformation queryPublicInformation(final QueryPublicInformationByIdUseCase.Query query) {
        return memberRepository.fetchPublicInformationById(query.memberId());
    }

    @Override
    public List<ParticipateStudy> queryParticipateStudy(final QueryParticipateStudyByIdUseCase.Query query) {
        return memberRepository.fetchParticipateStudyById(query.memberId());
    }

    @Override
    public List<GraduatedStudy> queryGraduatedStudy(final QueryGraduatedStudyByIdUseCase.Query query) {
        return memberRepository.fetchGraduatedStudyById(query.memberId());
    }

    @Override
    public List<ReceivedReview> queryReceivedReview(final QueryReceivedReviewByIdUseCase.Query query) {
        return memberRepository.fetchReceivedReviewById(query.memberId());
    }

    @Override
    public List<AttendanceRatio> queryAttendanceRatio(final QueryAttendanceRatioByIdUseCase.Query query) {
        return memberRepository.fetchAttendanceRatioById(query.memberId());
    }
}
