package com.kgu.studywithme.member.application.usecase;

import com.kgu.studywithme.member.application.usecase.query.GetAppliedStudyById;
import com.kgu.studywithme.member.application.usecase.query.GetLikeMarkedStudyById;
import com.kgu.studywithme.member.application.usecase.query.GetPrivateInformationById;
import com.kgu.studywithme.member.domain.repository.query.MemberInformationRepository;
import com.kgu.studywithme.member.domain.repository.query.dto.AppliedStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.LikeMarkedStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.MemberPrivateInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberPrivateQueryUseCase {
    private final MemberInformationRepository memberInformationRepository;

    public MemberPrivateInformation getPrivateInformationById(final GetPrivateInformationById query) {
        return memberInformationRepository.fetchPrivateInformationById(query.memberId());
    }

    public List<AppliedStudy> getAppliedStudyById(final GetAppliedStudyById query) {
        return memberInformationRepository.fetchAppliedStudyById(query.memberId());
    }

    public List<LikeMarkedStudy> getLikeMarkedStudyById(final GetLikeMarkedStudyById query) {
        return memberInformationRepository.fetchLikeMarkedStudyById(query.memberId());
    }
}
