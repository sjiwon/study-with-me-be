package com.kgu.studywithme.member.presentation;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.AppliedStudy;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.LikeMarkedStudy;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.MemberPrivateInformation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member -> MemberPrivateInformationApiController 테스트")
class MemberPrivateInformationApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("사용자 기본 Private 정보 조회 API [GET /api/members/me] - AccessToken 필수")
    class getInformation {
        private static final String BASE_URL = "/api/members/me";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("사용자 기본 Private 정보를 조회한다")
        void success() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);

            final Member member = JIWON.toMember().apply(1L, LocalDateTime.now());
            final MemberPrivateInformation response = new MemberPrivateInformation(
                    member.getId(),
                    member.getName(),
                    member.getNicknameValue(),
                    member.getEmailValue(),
                    member.getBirth(),
                    member.getPhone(),
                    member.getGender().getValue(),
                    member.getRegion(),
                    member.getScore(),
                    member.isEmailOptIn(),
                    member.getInterests()
                            .stream()
                            .map(Category::getName)
                            .toList()
            );
            given(queryPrivateInformationByIdUseCase.queryPrivateInformation(any())).willReturn(response);

            // when
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Query/Private/BasicInformation",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    responseFields(
                                            fieldWithPath("id")
                                                    .description("사용자 ID(PK)"),
                                            fieldWithPath("name")
                                                    .description("사용자 이름"),
                                            fieldWithPath("nickname")
                                                    .description("사용자 닉네임"),
                                            fieldWithPath("email")
                                                    .description("사용자 이메일"),
                                            fieldWithPath("birth")
                                                    .description("사용자 생년월일"),
                                            fieldWithPath("phone")
                                                    .description("사용자 전화번호"),
                                            fieldWithPath("gender")
                                                    .description("사용자 성별"),
                                            fieldWithPath("region.province")
                                                    .description("거주지 [경기도, 강원도, ...]"),
                                            fieldWithPath("region.city")
                                                    .description("거주지 [안양시, 수원시, ...]"),
                                            fieldWithPath("score")
                                                    .description("사용자 점수"),
                                            fieldWithPath("emailOptIn")
                                                    .description("이메일 수신 동의 여부"),
                                            fieldWithPath("interests[]")
                                                    .description("사용자 관심사 목록")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자가 신청한 스터디 리스트 조회 API [GET /api/members/me/studies/apply] - AccessToken 필수")
    class getApplyStudy {
        private static final String BASE_URL = "/api/members/me/studies/apply";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("사용자가 신청한 스터디 리스트를 조회한다")
        void success() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);
            given(queryAppliedStudyByIdUseCase.queryAppliedStudy(any()))
                    .willReturn(
                            List.of(
                                    new AppliedStudy(
                                            1L,
                                            SPRING.getName(),
                                            SPRING.getCategory().getName(),
                                            SPRING.getThumbnail().getImageName(),
                                            SPRING.getThumbnail().getBackground()
                                    ),
                                    new AppliedStudy(
                                            2L,
                                            JPA.getName(),
                                            JPA.getCategory().getName(),
                                            JPA.getThumbnail().getImageName(),
                                            JPA.getThumbnail().getBackground()
                                    )
                            )
                    );

            // when
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Query/Private/AppliedStudy",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    responseFields(
                                            fieldWithPath("result[].id")
                                                    .description("신청한 스터디 ID(PK)"),
                                            fieldWithPath("result[].name")
                                                    .description("신청한 스터디명"),
                                            fieldWithPath("result[].category")
                                                    .description("신청한 스터디 카테고리"),
                                            fieldWithPath("result[].thumbnail")
                                                    .description("신청한 스터디 썸네일 이미지"),
                                            fieldWithPath("result[].thumbnailBackground")
                                                    .description("신청한 스터디 썸네일 배경색")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자가 찜한 스터디 리스트 조회 API [GET /api/members/me/studies/favorite] - AccessToken 필수")
    class getFavoriteStudy {
        private static final String BASE_URL = "/api/members/me/studies/favorite";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("사용자가 찜한 스터디 리스트를 조회한다")
        void success() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);
            given(queryLikeMarkedStudyByIdUseCase.queryLikeMarkedStudy(any()))
                    .willReturn(
                            List.of(
                                    new LikeMarkedStudy(
                                            1L,
                                            SPRING.getName(),
                                            SPRING.getCategory().getName(),
                                            SPRING.getThumbnail().getImageName(),
                                            SPRING.getThumbnail().getBackground()
                                    ),
                                    new LikeMarkedStudy(
                                            2L,
                                            JPA.getName(),
                                            JPA.getCategory().getName(),
                                            JPA.getThumbnail().getImageName(),
                                            JPA.getThumbnail().getBackground()
                                    )
                            )
                    );

            // when
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Query/Private/LikeMarkedStudy",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    responseFields(
                                            fieldWithPath("result[].id")
                                                    .description("찜한 스터디 ID(PK)"),
                                            fieldWithPath("result[].name")
                                                    .description("찜한 스터디명"),
                                            fieldWithPath("result[].category")
                                                    .description("찜한 스터디 카테고리"),
                                            fieldWithPath("result[].thumbnail")
                                                    .description("찜한 스터디 썸네일 이미지"),
                                            fieldWithPath("result[].thumbnailBackground")
                                                    .description("찜한 스터디 썸네일 배경색")
                                    )
                            )
                    );
        }
    }
}
