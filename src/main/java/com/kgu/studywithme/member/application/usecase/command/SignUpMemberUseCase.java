package com.kgu.studywithme.member.application.usecase.command;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.member.domain.Address;
import com.kgu.studywithme.member.domain.Email;
import com.kgu.studywithme.member.domain.Gender;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.member.domain.Phone;

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
