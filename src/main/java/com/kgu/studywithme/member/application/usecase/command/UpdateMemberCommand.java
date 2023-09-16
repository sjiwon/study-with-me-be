package com.kgu.studywithme.member.application.usecase.command;

import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.member.domain.model.Address;
import com.kgu.studywithme.member.domain.model.Nickname;
import com.kgu.studywithme.member.domain.model.Phone;

import java.util.Set;

public record UpdateMemberCommand(
        Long memberId,
        Nickname nickname,
        Phone phone,
        Address address,
        boolean emailOptIn,
        Set<Category> interests
) {
}
