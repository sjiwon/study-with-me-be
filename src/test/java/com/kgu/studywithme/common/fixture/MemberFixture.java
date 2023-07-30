package com.kgu.studywithme.common.fixture;

import com.kgu.studywithme.auth.application.dto.LoginResponse;
import com.kgu.studywithme.auth.application.dto.MemberInfo;
import com.kgu.studywithme.auth.infrastructure.oauth.google.response.GoogleUserResponse;
import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.member.domain.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.kgu.studywithme.category.domain.Category.*;
import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.REFRESH_TOKEN;
import static com.kgu.studywithme.member.domain.Gender.MALE;

@Getter
@RequiredArgsConstructor
public enum MemberFixture {
    JIWON(
            "서지원", new Nickname("서지원"), new Email("sjiwon4491@gmail.com"),
            LocalDate.of(2000, 1, 18), MALE,
            new Region("경기도", "안양시"), true,
            new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))
    ),
    GHOST(
            "고스트", new Nickname("고스트"), new Email("ghost@gmail.com"),
            LocalDate.of(2002, 11, 18), MALE,
            new Region("경기도", "안양시"), true,
            new HashSet<>(Set.of(APTITUDE_NCS, CERTIFICATION, ETC))
    ),
    ANONYMOUS(
            "익명", new Nickname("익명"), new Email("anonymous@gmail.com"),
            LocalDate.of(1970, 1, 18), MALE,
            new Region("경기도", "수원시"), false,
            new HashSet<>(Set.of(APTITUDE_NCS, ETC))
    ),


    DUMMY1(
            "더미1", new Nickname("더미1"), new Email("dummy1@gmail.com"),
            LocalDate.of(1971, 1, 18), MALE,
            new Region("경기도", "안양시"), true,
            new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))
    ),
    DUMMY2(
            "더미2", new Nickname("더미2"), new Email("dummy2@gmail.com"),
            LocalDate.of(1972, 1, 18), MALE,
            new Region("경기도", "안양시"), true,
            new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))
    ),
    DUMMY3(
            "더미3", new Nickname("더미3"), new Email("dummy3@gmail.com"),
            LocalDate.of(1983, 1, 18), MALE,
            new Region("경기도", "안양시"), true,
            new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))
    ),
    DUMMY4(
            "더미4", new Nickname("더미4"), new Email("dummy4@gmail.com"),
            LocalDate.of(2004, 1, 18), MALE,
            new Region("경기도", "안양시"), true,
            new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))
    ),
    DUMMY5(
            "더미5", new Nickname("더미5"), new Email("dummy5@gmail.com"),
            LocalDate.of(1993, 1, 18), MALE,
            new Region("경기도", "안양시"), true,
            new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))
    ),
    DUMMY6(
            "더미6", new Nickname("더미6"), new Email("dummy6@gmail.com"),
            LocalDate.of(1997, 1, 18), MALE,
            new Region("경기도", "안양시"), true,
            new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))
    ),
    DUMMY7(
            "더미7", new Nickname("더미7"), new Email("dummy7@gmail.com"),
            LocalDate.of(1996, 1, 18), MALE,
            new Region("경기도", "안양시"), true,
            new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))
    ),
    DUMMY8(
            "더미8", new Nickname("더미8"), new Email("dummy8@gmail.com"),
            LocalDate.of(1996, 1, 18), MALE,
            new Region("경기도", "안양시"), true,
            new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))
    ),
    DUMMY9(
            "더미9", new Nickname("더미9"), new Email("dummy9@gmail.com"),
            LocalDate.of(1999, 1, 18), MALE,
            new Region("경기도", "안양시"), true,
            new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))
    ),
    ;

    private final String name;
    private final Nickname nickname;
    private final Email email;
    private final LocalDate birth;
    private final Gender gender;
    private final Region region;
    private final boolean emailOptIn;
    private final Set<Category> interests;

    public Member toMember() {
        return Member.createMember(
                name,
                nickname,
                email,
                birth,
                generateRandomPhoneNumber(),
                gender,
                region,
                emailOptIn,
                interests
        );
    }

    private static String generateRandomPhoneNumber() {
        return "010" + "-" +
                (int) (Math.random() * 9000 + 1000) + "-" +
                (int) (Math.random() * 9000 + 1000);
    }

    public LoginResponse toLoginResponse() {
        return new LoginResponse(
                new MemberInfo(toMember().apply(1L, LocalDateTime.now())),
                ACCESS_TOKEN,
                REFRESH_TOKEN
        );
    }

    public GoogleUserResponse toGoogleUserResponse() {
        return new GoogleUserResponse(name, email.getValue(), "google_profile_url");
    }
}
