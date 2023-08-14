package com.kgu.studywithme.member.application.service;

import com.kgu.studywithme.member.application.usecase.query.QueryAppliedStudyByIdUseCase;
import com.kgu.studywithme.member.application.usecase.query.QueryLikeMarkedStudyByIdUseCase;
import com.kgu.studywithme.member.application.usecase.query.QueryPrivateInformationByIdUseCase;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.AppliedStudy;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.LikeMarkedStudy;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.MemberPrivateInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberPrivateQueryService implements
        QueryPrivateInformationByIdUseCase,
        QueryAppliedStudyByIdUseCase,
        QueryLikeMarkedStudyByIdUseCase {

    private final MemberRepository memberRepository;

    @Override
    public MemberPrivateInformation invoke(final QueryPrivateInformationByIdUseCase.Query query) {
        return memberRepository.fetchPrivateInformationById(query.memberId());
    }

    @Override
    public List<AppliedStudy> invoke(final QueryAppliedStudyByIdUseCase.Query query) {
        return memberRepository.fetchAppliedStudyById(query.memberId());
    }

    @Override
    public List<LikeMarkedStudy> invoke(final QueryLikeMarkedStudyByIdUseCase.Query query) {
        return memberRepository.fetchLikeMarkedStudyById(query.memberId());
    }
}
