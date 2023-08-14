package com.kgu.studywithme.member.application.usecase.query;

import com.kgu.studywithme.member.infrastructure.repository.query.dto.MemberPrivateInformation;

public interface QueryPrivateInformationByIdUseCase {
    MemberPrivateInformation invoke(final Query query);

    record Query(
            Long memberId
    ) {
    }
}
