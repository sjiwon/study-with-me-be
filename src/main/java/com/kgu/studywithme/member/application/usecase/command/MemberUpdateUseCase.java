package com.kgu.studywithme.member.application.usecase.command;

import com.kgu.studywithme.category.domain.Category;

import java.util.Set;

public interface MemberUpdateUseCase {
    void update(Command command);

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
