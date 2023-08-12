package com.kgu.studywithme.member.infrastructure.repository.query.dto;

import com.kgu.studywithme.member.domain.Email;
import com.kgu.studywithme.member.domain.Gender;
import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.member.domain.Region;
import com.kgu.studywithme.member.domain.Score;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class MemberPublicInformation {
    private final Long id;
    private final String name;
    private final String nickname;
    private final String email;
    private final LocalDate birth;
    private final String gender;
    private final Region region;
    private final int score;
    private List<String> interests;

    @QueryProjection
    public MemberPublicInformation(
            final Long id,
            final String name,
            final Nickname nickname,
            final Email email,
            final LocalDate birth,
            final Gender gender,
            final Region region,
            final Score score
    ) {
        this.id = id;
        this.name = name;
        this.nickname = nickname.getValue();
        this.email = email.getValue();
        this.birth = birth;
        this.gender = gender.getValue();
        this.region = region;
        this.score = score.getValue();
    }

    public void applyInterests(final List<String> interests) {
        this.interests = interests;
    }
}
