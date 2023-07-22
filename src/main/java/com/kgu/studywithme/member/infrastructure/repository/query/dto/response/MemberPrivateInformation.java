package com.kgu.studywithme.member.infrastructure.repository.query.dto.response;

import com.kgu.studywithme.member.domain.*;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class MemberPrivateInformation {
    private final Long id;
    private final String name;
    private final String nickname;
    private final String email;
    private final LocalDate birth;
    private final String phone;
    private final String gender;
    private final Region region;
    private final int score;
    private final boolean emailOptIn;
    private List<String> interests;

    @QueryProjection
    public MemberPrivateInformation(
            final Long id,
            final String name,
            final Nickname nickname,
            final Email email,
            final LocalDate birth,
            final String phone,
            final Gender gender,
            final Region region,
            final Score score,
            final boolean emailOptIn
    ) {
        this.id = id;
        this.name = name;
        this.nickname = nickname.getValue();
        this.email = email.getValue();
        this.birth = birth;
        this.phone = phone;
        this.gender = gender.getValue();
        this.region = region;
        this.score = score.getValue();
        this.emailOptIn = emailOptIn;
    }

    public void applyInterests(final List<String> interests) {
        this.interests = interests;
    }
}