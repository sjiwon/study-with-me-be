package com.kgu.studywithme.acceptance.member;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.common.fixture.MemberFixture;
import com.kgu.studywithme.member.presentation.dto.request.SignUpMemberRequest;
import com.kgu.studywithme.member.presentation.dto.request.UpdateMemberRequest;
import com.kgu.studywithme.memberreport.presentation.dto.request.ReportMemberRequest;
import com.kgu.studywithme.memberreview.presentation.dto.request.UpdateMemberReviewRequest;
import com.kgu.studywithme.memberreview.presentation.dto.request.WriteMemberReviewRequest;
import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.stream.Collectors;

import static com.kgu.studywithme.acceptance.CommonRequestFixture.*;

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

    public static ValidatableResponse 사용자_정보를_수정한다(
            final String accessToken,
            final MemberFixture fixture
    ) {
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

    public static ValidatableResponse 해당_사용자에게_리뷰를_작성한다(
            final String accessToken,
            final Long revieweeId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/members/{revieweeId}/review")
                .build(revieweeId)
                .getPath();

        final WriteMemberReviewRequest request = new WriteMemberReviewRequest("Good!!");

        return postRequest(accessToken, request, uri);
    }

    public static ValidatableResponse 작성한_리뷰를_수정한다(
            final String accessToken,
            final Long revieweeId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/members/{revieweeId}/review")
                .build(revieweeId)
                .getPath();

        final UpdateMemberReviewRequest request = new UpdateMemberReviewRequest("Bad...");

        return patchRequest(accessToken, request, uri);
    }

    public static ValidatableResponse 해당_사용자를_신고한다(
            final String accessToken,
            final Long reporteeId
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/members/{reporteeId}/report")
                .build(reporteeId)
                .getPath();

        final ReportMemberRequest request = new ReportMemberRequest("매번 지각하고 과제도 제출안해요");

        return postRequest(accessToken, request, uri);
    }

    public static ValidatableResponse 사용자_Private_정보를_조회한다(final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/members/me")
                .build()
                .toUri()
                .getPath();

        return getRequest(accessToken, uri);
    }

    public static ValidatableResponse 사용자_Public_정보를_조회한다(final Long memberId) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/members/{memberId}")
                .build(memberId)
                .getPath();

        return getRequest(uri);
    }

    public static ValidatableResponse 사용자가_받은_리뷰를_조회한다(final Long memberId) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/members/{memberId}/reviews")
                .build(memberId)
                .getPath();

        return getRequest(uri);
    }

    public static ValidatableResponse 사용자가_신청한_스터디를_조회한다(final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/members/me/studies/apply")
                .build()
                .toUri()
                .getPath();

        return getRequest(accessToken, uri);
    }

    public static ValidatableResponse 사용자가_찜한_스터디를_조회한다(final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/members/me/studies/favorite")
                .build()
                .toUri()
                .getPath();

        return getRequest(accessToken, uri);
    }

    public static ValidatableResponse 사용자가_참여하고_있는_스터디를_조회한다(final Long memberId) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/members/{memberId}/studies/participate")
                .build(memberId)
                .getPath();

        return getRequest(uri);
    }

    public static ValidatableResponse 사용자가_졸업한_스터디를_조회한다(final Long memberId) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/members/{memberId}/studies/graduated")
                .build(memberId)
                .getPath();

        return getRequest(uri);
    }

    public static ValidatableResponse 사용자의_스터디_출석률을_조회한다(final Long memberId) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/members/{memberId}/attendances")
                .build(memberId)
                .getPath();

        return getRequest(uri);
    }
}
