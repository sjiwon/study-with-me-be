package com.kgu.studywithme.common.fixture;

import com.kgu.studywithme.acceptance.member.MemberAcceptanceFixture;
import com.kgu.studywithme.auth.domain.model.AuthMember;
import com.kgu.studywithme.auth.domain.model.AuthToken;
import com.kgu.studywithme.auth.infrastructure.oauth.google.response.GoogleUserResponse;
import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.member.domain.model.Address;
import com.kgu.studywithme.member.domain.model.Email;
import com.kgu.studywithme.member.domain.model.Gender;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.model.Nickname;
import com.kgu.studywithme.member.domain.model.Phone;
import io.restassured.response.ValidatableResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.kgu.studywithme.acceptance.auth.AuthAcceptanceFixture.Google_OAuth_로그인을_진행한다;
import static com.kgu.studywithme.auth.utils.TokenResponseWriter.REFRESH_TOKEN_COOKIE;
import static com.kgu.studywithme.category.domain.model.Category.APTITUDE_NCS;
import static com.kgu.studywithme.category.domain.model.Category.CERTIFICATION;
import static com.kgu.studywithme.category.domain.model.Category.ETC;
import static com.kgu.studywithme.category.domain.model.Category.INTERVIEW;
import static com.kgu.studywithme.category.domain.model.Category.LANGUAGE;
import static com.kgu.studywithme.category.domain.model.Category.PROGRAMMING;
import static com.kgu.studywithme.common.fixture.OAuthFixture.getAuthorizationCodeByIdentifier;
import static com.kgu.studywithme.common.utils.OAuthUtils.GOOGLE_PROVIDER;
import static com.kgu.studywithme.common.utils.OAuthUtils.REDIRECT_URI;
import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.REFRESH_TOKEN;
import static com.kgu.studywithme.member.domain.model.Gender.MALE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Getter
@RequiredArgsConstructor
public enum MemberFixture {
    JIWON(
            "서지원", new Nickname("서지원"), new Email("sjiwon4491@gmail.com", true),
            LocalDate.of(2000, 1, 18), new Phone("010-1234-5678"),
            MALE, new Address("경기도", "안양시"),
            new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))
    ),
    GHOST(
            "고스트", new Nickname("고스트"), new Email("ghost@gmail.com", true),
            LocalDate.of(2002, 11, 18), new Phone("010-2345-6789"),
            MALE, new Address("경기도", "안양시"),
            new HashSet<>(Set.of(APTITUDE_NCS, CERTIFICATION, ETC))
    ),
    ANONYMOUS(
            "익명", new Nickname("익명"), new Email("anonymous@gmail.com", false),
            LocalDate.of(1970, 1, 18), new Phone("010-3456-7890"),
            MALE, new Address("경기도", "수원시"),
            new HashSet<>(Set.of(APTITUDE_NCS, ETC))
    ),


    DUMMY1(
            "더미1", new Nickname("더미1"), new Email("dummy1@gmail.com", true),
            LocalDate.of(1971, 1, 18), new Phone("010-1111-0001"),
            MALE, new Address("경기도", "안양시"),
            new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))
    ),
    DUMMY2(
            "더미2", new Nickname("더미2"), new Email("dummy2@gmail.com", true),
            LocalDate.of(1972, 1, 18), new Phone("010-1111-0002"),
            MALE, new Address("경기도", "안양시"),
            new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))
    ),
    DUMMY3(
            "더미3", new Nickname("더미3"), new Email("dummy3@gmail.com", true),
            LocalDate.of(1983, 1, 18), new Phone("010-1111-0003"),
            MALE, new Address("경기도", "안양시"),
            new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))
    ),
    DUMMY4(
            "더미4", new Nickname("더미4"), new Email("dummy4@gmail.com", true),
            LocalDate.of(2004, 1, 18), new Phone("010-1111-0004"),
            MALE, new Address("경기도", "안양시"),
            new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))
    ),
    DUMMY5(
            "더미5", new Nickname("더미5"), new Email("dummy5@gmail.com", true),
            LocalDate.of(1993, 1, 18), new Phone("010-1111-0005"),
            MALE, new Address("경기도", "안양시"),
            new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))
    ),
    DUMMY6(
            "더미6", new Nickname("더미6"), new Email("dummy6@gmail.com", true),
            LocalDate.of(1997, 1, 18), new Phone("010-1111-0006"),
            MALE, new Address("경기도", "안양시"),
            new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))
    ),
    DUMMY7(
            "더미7", new Nickname("더미7"), new Email("dummy7@gmail.com", true),
            LocalDate.of(1996, 1, 18), new Phone("010-1111-0007"),
            MALE, new Address("경기도", "안양시"),
            new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))
    ),
    DUMMY8(
            "더미8", new Nickname("더미8"), new Email("dummy8@gmail.com", true),
            LocalDate.of(1996, 1, 18), new Phone("010-1111-0008"),
            MALE, new Address("경기도", "안양시"),
            new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))
    ),
    DUMMY9(
            "더미9", new Nickname("더미9"), new Email("dummy9@gmail.com", true),
            LocalDate.of(1999, 1, 18), new Phone("010-1111-0009"),
            MALE, new Address("경기도", "안양시"),
            new HashSet<>(Set.of(LANGUAGE, INTERVIEW, PROGRAMMING))
    ),
    ;

    private final String name;
    private final Nickname nickname;
    private final Email email;
    private final LocalDate birth;
    private final Phone phone;
    private final Gender gender;
    private final Address address;
    private final Set<Category> interests;

    public Member toMember() {
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

    public GoogleUserResponse toGoogleUserResponse() {
        return new GoogleUserResponse(
                UUID.randomUUID().toString(),
                this.name,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                this.email.getValue(),
                true,
                "kr"
        );
    }

    public AuthMember toAuthMember() {
        return new AuthMember(
                new AuthMember.MemberInfo(toMember().apply(1L, LocalDateTime.now())),
                new AuthToken(ACCESS_TOKEN, REFRESH_TOKEN)
        );
    }

    public Long 회원가입을_진행한다() {
        return MemberAcceptanceFixture.회원가입을_진행한다(this)
                .extract()
                .jsonPath()
                .getLong("memberId");
    }

    public String 로그인을_진행하고_AccessToken을_추출한다() {
        final ValidatableResponse response = Google_OAuth_로그인을_진행한다(
                GOOGLE_PROVIDER,
                getAuthorizationCodeByIdentifier(this.getEmail().getValue()),
                REDIRECT_URI,
                UUID.randomUUID().toString()
        );

        final String token = response
                .extract()
                .header(AUTHORIZATION);

        return token.split(" ")[1];
    }

    public String 로그인을_진행하고_RefreshToken을_추출한다() {
        final ValidatableResponse response = Google_OAuth_로그인을_진행한다(
                GOOGLE_PROVIDER,
                getAuthorizationCodeByIdentifier(this.getEmail().getValue()),
                REDIRECT_URI,
                UUID.randomUUID().toString()
        );

        return response
                .extract()
                .cookie(REFRESH_TOKEN_COOKIE);
    }

    public List<String> 회원가입_후_Google_OAuth_로그인을_진행하고_Token을_추출한다() {
        MemberAcceptanceFixture.회원가입을_진행한다(this);

        final ValidatableResponse response = Google_OAuth_로그인을_진행한다(
                GOOGLE_PROVIDER,
                getAuthorizationCodeByIdentifier(this.getEmail().getValue()),
                REDIRECT_URI,
                UUID.randomUUID().toString()
        );

        final String accessToken = response
                .extract()
                .header(AUTHORIZATION)
                .split(" ")[1];
        final String refreshToken = response
                .extract()
                .cookie(REFRESH_TOKEN_COOKIE);
        return List.of(accessToken, refreshToken);
    }

    public String 회원가입_후_Google_OAuth_로그인을_진행하고_AccessToken을_추출한다() {
        MemberAcceptanceFixture.회원가입을_진행한다(this);

        final ValidatableResponse response = Google_OAuth_로그인을_진행한다(
                GOOGLE_PROVIDER,
                getAuthorizationCodeByIdentifier(this.getEmail().getValue()),
                REDIRECT_URI,
                UUID.randomUUID().toString()
        );

        final String token = response
                .extract()
                .header(AUTHORIZATION);

        return token.split(" ")[1];
    }

    public String 회원가입_후_Google_OAuth_로그인을_진행하고_RefreshToken을_추출한다() {
        MemberAcceptanceFixture.회원가입을_진행한다(this);

        final ValidatableResponse response = Google_OAuth_로그인을_진행한다(
                GOOGLE_PROVIDER,
                getAuthorizationCodeByIdentifier(this.getEmail().getValue()),
                REDIRECT_URI,
                UUID.randomUUID().toString()
        );

        return response
                .extract()
                .cookie(REFRESH_TOKEN_COOKIE);
    }
}
