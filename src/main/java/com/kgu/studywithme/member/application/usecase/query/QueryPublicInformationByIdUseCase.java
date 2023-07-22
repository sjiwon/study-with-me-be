package com.kgu.studywithme.member.application.usecase.query;

import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.MemberPublicInformation;

public interface QueryPublicInformationByIdUseCase {
    MemberPublicInformation queryPublicInformation(final Query query);

    record Query(
            Long memberId
    ) {
    }
}