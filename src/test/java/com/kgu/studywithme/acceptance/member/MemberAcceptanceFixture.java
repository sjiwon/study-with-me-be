package com.kgu.studywithme.acceptance.member;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.common.fixture.MemberFixture;
import com.kgu.studywithme.member.presentation.dto.request.SignUpMemberRequest;
import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.stream.Collectors;

import static com.kgu.studywithme.acceptance.CommonRequestFixture.postRequest;

public class MemberAcceptanceFixture {
    public static ValidatableResponse 회원가입을_진행한다(final MemberFixture fixture) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/member")
                .build()
                .toUri()
                .getPath();

        final SignUpMemberRequest request = new SignUpMemberRequest(
                fixture.getName(),
                fixture.getNickname().getValue(),
                fixture.getEmail().getValue(),
                fixture.getBirth(),
                fixture.getPhone(),
                fixture.getGender().getSimpleValue(),
                fixture.getRegion().getProvince(),
                fixture.getRegion().getCity(),
                true,
                fixture.getInterests()
                        .stream()
                        .map(Category::getId)
                        .collect(Collectors.toSet())
        );

        return postRequest(request, uri);
    }
}
