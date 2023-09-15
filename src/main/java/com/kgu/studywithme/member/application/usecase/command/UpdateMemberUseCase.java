package com.kgu.studywithme.member.application.usecase.command;

import com.kgu.studywithme.category.domain.model.Category;

import java.util.Set;

public interface UpdateMemberUseCase {
    void invoke(final Command command);

    record Command(
            Long memberId,
            String nickname,
            String phone,
            String province,
            String city,
            boolean emailOptIn,
            Set<Category> interests
    ) {
    }
}
