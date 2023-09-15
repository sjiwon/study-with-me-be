package com.kgu.studywithme.member.application.usecase.command;

import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.member.domain.model.Address;
import com.kgu.studywithme.member.domain.model.Email;
import com.kgu.studywithme.member.domain.model.Gender;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.model.Nickname;
import com.kgu.studywithme.member.domain.model.Phone;

import java.time.LocalDate;
import java.util.Set;

public interface SignUpMemberUseCase {
    Long invoke(final Command command);

    record Command(
            String name,
            Nickname nickname,
            Email email,
            LocalDate birth,
            Phone phone,
            Gender gender,
            Address address,
            Set<Category> interests
    ) {
        public Member toDomain() {
            return Member.createMember(
                    name,
                    nickname,
                    email,
                    birth,
                    phone,
                    gender,
                    address,
                    interests
            );
        }
    }
}
