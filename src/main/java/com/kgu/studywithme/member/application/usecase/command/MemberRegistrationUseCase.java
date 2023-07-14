package com.kgu.studywithme.member.application.usecase.command;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.member.domain.*;

import java.time.LocalDate;
import java.util.Set;

public interface MemberRegistrationUseCase {
    Long registration(Command command);

    record Command(
            String name,
            Nickname nickname,
            Email email,
            LocalDate birth,
            String phone,
            Gender gender,
            Region region,
            boolean emailOptIn,
            Set<Category> interests
    ) {
        public Member toEntity() {
            return Member.createMember(
                    name,
                    nickname,
                    email,
                    birth,
                    phone,
                    gender,
                    region,
                    emailOptIn,
                    interests
            );
        }
    }
}
