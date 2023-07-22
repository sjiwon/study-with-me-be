package com.kgu.studywithme.member.application.service;

import com.kgu.studywithme.member.application.usecase.query.QueryAppliedStudyByIdUseCase;
import com.kgu.studywithme.member.application.usecase.query.QueryLikeMarkedStudyByIdUseCase;
import com.kgu.studywithme.member.application.usecase.query.QueryPrivateInformationByIdUseCase;
import com.kgu.studywithme.member.infrastructure.repository.query.MemberInformationRepository;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.AppliedStudy;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.LikeMarkedStudy;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.MemberPrivateInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberPrivateQueryService implements
        QueryPrivateInformationByIdUseCase,
        QueryAppliedStudyByIdUseCase,
        QueryLikeMarkedStudyByIdUseCase {

    private final MemberInformationRepository memberInformationRepository;

    @Override
    public MemberPrivateInformation queryPrivateInformation(final QueryPrivateInformationByIdUseCase.Query query) {
        return memberInformationRepository.fetchPrivateInformationById(query.memberId());
    }

    @Override
    public List<AppliedStudy> queryAppliedStudy(final QueryAppliedStudyByIdUseCase.Query query) {
        return memberInformationRepository.fetchAppliedStudyById(query.memberId());
    }

    @Override
    public List<LikeMarkedStudy> queryLikeMarkedStudy(final QueryLikeMarkedStudyByIdUseCase.Query query) {
        return memberInformationRepository.fetchLikeMarkedStudyById(query.memberId());
    }
}
