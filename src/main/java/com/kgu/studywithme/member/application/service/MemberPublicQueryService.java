package com.kgu.studywithme.member.application.service;

import com.kgu.studywithme.member.application.usecase.query.*;
import com.kgu.studywithme.member.infrastructure.repository.query.MemberInformationRepository;
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

    private final MemberInformationRepository memberInformationRepository;

    @Override
    public MemberPublicInformation queryPublicInformation(final QueryPublicInformationByIdUseCase.Query query) {
        return memberInformationRepository.fetchPublicInformationById(query.memberId());
    }

    @Override
    public List<ParticipateStudy> queryParticipateStudy(final QueryParticipateStudyByIdUseCase.Query query) {
        return memberInformationRepository.fetchParticipateStudyById(query.memberId());
    }

    @Override
    public List<GraduatedStudy> queryGraduatedStudy(final QueryGraduatedStudyByIdUseCase.Query query) {
        return memberInformationRepository.fetchGraduatedStudyById(query.memberId());
    }

    @Override
    public List<ReceivedReview> queryReceivedReview(final QueryReceivedReviewByIdUseCase.Query query) {
        return memberInformationRepository.fetchReceivedReviewById(query.memberId());
    }

    @Override
    public List<AttendanceRatio> queryAttendanceRatio(final QueryAttendanceRatioByIdUseCase.Query query) {
        return memberInformationRepository.fetchAttendanceRatioById(query.memberId());
    }
}
