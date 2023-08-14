package com.kgu.studywithme.member.application.usecase.query;

import com.kgu.studywithme.member.infrastructure.repository.query.dto.MemberPublicInformation;

public interface QueryPublicInformationByIdUseCase {
    MemberPublicInformation invoke(final Query query);

    record Query(
            Long memberId
    ) {
    }
}
