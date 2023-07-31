package com.kgu.studywithme.acceptance.fixture;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.member.presentation.dto.request.SignUpMemberRequest;
import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.stream.Collectors;

import static com.kgu.studywithme.acceptance.fixture.CommonRequestFixture.postRequest;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;

public class MemberAcceptanceFixture {
    public static ValidatableResponse 회원가입을_진행한다() {
        final URI uri = UriComponentsBuilder
                .fromPath("/api/member")
                .build()
                .toUri();
        final SignUpMemberRequest request = new SignUpMemberRequest(
                JIWON.getName(),
                JIWON.getNickname().getValue(),
                JIWON.getEmail().getValue(),
                JIWON.getBirth(),
                "010-1234-5678",
                JIWON.getGender().getSimpleValue(),
                JIWON.getRegion().getProvince(),
                JIWON.getRegion().getCity(),
                true,
                JIWON.getInterests()
                        .stream()
                        .map(Category::getId)
                        .collect(Collectors.toSet())
        );

        return postRequest(request, uri.getPath());
    }
}
