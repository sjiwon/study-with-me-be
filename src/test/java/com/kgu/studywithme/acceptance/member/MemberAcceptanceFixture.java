package com.kgu.studywithme.acceptance.member;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.common.fixture.MemberFixture;
import com.kgu.studywithme.member.presentation.dto.request.SignUpMemberRequest;
import com.kgu.studywithme.member.presentation.dto.request.UpdateMemberRequest;
import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.stream.Collectors;

import static com.kgu.studywithme.acceptance.CommonRequestFixture.patchRequest;
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

    public static ValidatableResponse 사용자_정보를_수정한다(final String accessToken, final MemberFixture fixture) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/members/me")
                .build()
                .toUri()
                .getPath();

        final UpdateMemberRequest request = new UpdateMemberRequest(
                fixture.getNickname().getValue(),
                fixture.getPhone(),
                fixture.getRegion().getProvince(),
                fixture.getRegion().getCity(),
                fixture.isEmailOptIn(),
                fixture.getInterests()
                        .stream()
                        .map(Category::getId)
                        .collect(Collectors.toSet())
        );

        return patchRequest(accessToken, request, uri);
    }
}
