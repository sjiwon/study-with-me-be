package com.kgu.studywithme.member.domain.repository.query.dto;

import com.kgu.studywithme.member.domain.model.Address;
import com.kgu.studywithme.member.domain.model.Email;
import com.kgu.studywithme.member.domain.model.Gender;
import com.kgu.studywithme.member.domain.model.Nickname;
import com.kgu.studywithme.member.domain.model.Phone;
import com.kgu.studywithme.member.domain.model.Score;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class MemberPrivateInformation {
    private Long id;
    private String name;
    private String nickname;
    private String email;
    private boolean emailOptIn;
    private LocalDate birth;
    private String phone;
    private String gender;
    private AddressInfo address;
    private int score;
    private List<String> interests;

    @QueryProjection
    public MemberPrivateInformation(
            final Long id,
            final String name,
            final Nickname nickname,
            final Email email,
            final LocalDate birth,
            final Phone phone,
            final Gender gender,
            final Address address,
            final Score score
    ) {
        this.id = id;
        this.name = name;
        this.nickname = nickname.getValue();
        this.email = email.getValue();
        this.emailOptIn = email.isEmailOptIn();
        this.birth = birth;
        this.phone = phone.getValue();
        this.gender = gender.getValue();
        this.address = new AddressInfo(address);
        this.score = score.getValue();
    }

    public void applyInterests(final List<String> interests) {
        this.interests = interests;
    }
}
