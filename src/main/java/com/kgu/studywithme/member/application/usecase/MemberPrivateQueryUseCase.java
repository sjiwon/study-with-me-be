package com.kgu.studywithme.member.application.usecase;

import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.global.cache.CacheKeyName;
import com.kgu.studywithme.member.application.usecase.query.GetAppliedStudyById;
import com.kgu.studywithme.member.application.usecase.query.GetLikeMarkedStudyById;
import com.kgu.studywithme.member.application.usecase.query.GetPrivateInformationById;
import com.kgu.studywithme.member.domain.repository.query.MemberInformationRepository;
import com.kgu.studywithme.member.domain.repository.query.dto.AppliedStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.LikeMarkedStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.MemberPrivateInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class MemberPrivateQueryUseCase {
    private final MemberInformationRepository memberInformationRepository;

    @Cacheable(
            value = CacheKeyName.MEMBER,
            key = "#query.memberId()",
            cacheManager = "memberInfoCacheManager",
            unless = "#result != null"
    )
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
