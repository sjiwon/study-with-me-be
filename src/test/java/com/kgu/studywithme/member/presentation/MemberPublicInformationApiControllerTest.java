package com.kgu.studywithme.member.presentation;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member -> MemberPublicInformationApiController 테스트")
class MemberPublicInformationApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("사용자 기본 Public 정보 조회 API [GET /api/members/{memberId}]")
    class getInformation {
        private static final String BASE_URL = "/api/members/{memberId}";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("사용자 기본 Public 정보를 조회한다")
        void success() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);

            final Member member = JIWON.toMember().apply(1L, LocalDateTime.now());
            final MemberPublicInformation response = new MemberPublicInformation(
                    member.getId(),
                    member.getName(),
                    member.getNickname().getValue(),
                    member.getEmail().getValue(),
                    member.getBirth(),
                    member.getGender().getValue(),
                    member.getRegion(),
                    member.getScore(),
                    member.getInterests()
                            .stream()
                            .map(Category::getName)
                            .toList()
            );
            given(queryPublicInformationByIdUseCase.queryPublicInformation(any())).willReturn(response);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Query/Public/BasicInformation",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result.id").description("사용자 ID(PK)"),
                                            fieldWithPath("result.name").description("사용자 이름"),
                                            fieldWithPath("result.nickname").description("사용자 닉네임"),
                                            fieldWithPath("result.email").description("사용자 이메일"),
                                            fieldWithPath("result.birth").description("사용자 생년월일"),
                                            fieldWithPath("result.gender").description("사용자 성별"),
                                            fieldWithPath("result.region.province").description("거주지 [경기도, 강원도, ...]"),
                                            fieldWithPath("result.region.city").description("거주지 [안양시, 수원시, ...]"),
                                            fieldWithPath("result.score").description("사용자 점수"),
                                            fieldWithPath("result.interests[]").description("사용자 관심사 목록")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자가 참여중인 스터디 리스트 조회 API [GET /api/members/{memberId}/studies/participate]")
    class getParticipateStudy {
        private static final String BASE_URL = "/api/members/{memberId}/studies/participate";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("사용자가 참여중인 스터디 리스트를 조회한다")
        void success() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);
            given(queryParticipateStudyByIdUseCase.queryParticipateStudy(any()))
                    .willReturn(
                            List.of(
                                    new ParticipateStudy(
                                            1L,
                                            SPRING.getName(),
                                            SPRING.getCategory().getName(),
                                            SPRING.getThumbnail().getImageName(),
                                            SPRING.getThumbnail().getBackground()
                                    ),
                                    new ParticipateStudy(
                                            2L,
                                            JPA.getName(),
                                            JPA.getCategory().getName(),
                                            JPA.getThumbnail().getImageName(),
                                            JPA.getThumbnail().getBackground()
                                    )
                            )
                    );

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Query/Public/ParticipateStudy",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].id").description("참여중인 스터디 ID(PK)"),
                                            fieldWithPath("result[].name").description("참여중인 스터디명"),
                                            fieldWithPath("result[].category").description("참여중인 스터디 카테고리"),
                                            fieldWithPath("result[].thumbnail").description("참여중인 스터디 썸네일 이미지"),
                                            fieldWithPath("result[].thumbnailBackground").description("참여중인 스터디 썸네일 배경색")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자가 졸업한 스터디 리스트 조회 API [GET /api/members/{memberId}/studies/graduated]")
    class getGraduatedStudy {
        private static final String BASE_URL = "/api/members/{memberId}/studies/graduated";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("사용자가 졸업한 스터디 리스트를 조회한다")
        void success() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);
            given(queryGraduatedStudyByIdUseCase.queryGraduatedStudy(any()))
                    .willReturn(
                            List.of(
                                    new GraduatedStudy(
                                            1L,
                                            SPRING.getName(),
                                            SPRING.getCategory().getName(),
                                            SPRING.getThumbnail().getImageName(),
                                            SPRING.getThumbnail().getBackground(),
                                            new GraduatedStudy.WrittenReview(
                                                    1L,
                                                    "Good Study",
                                                    LocalDateTime.now(),
                                                    LocalDateTime.now()
                                            )
                                    ),
                                    new GraduatedStudy(
                                            2L,
                                            JPA.getName(),
                                            JPA.getCategory().getName(),
                                            JPA.getThumbnail().getImageName(),
                                            JPA.getThumbnail().getBackground(),
                                            new GraduatedStudy.WrittenReview(
                                                    1L,
                                                    "Good Study",
                                                    LocalDateTime.now(),
                                                    LocalDateTime.now()
                                            )
                                    )
                            )
                    );

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Query/Public/GraduatedStudy",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].id").description("졸업한 스터디 ID(PK)"),
                                            fieldWithPath("result[].name").description("졸업한 스터디명"),
                                            fieldWithPath("result[].category").description("졸업한 스터디 카테고리"),
                                            fieldWithPath("result[].thumbnail").description("졸업한 스터디 썸네일 이미지"),
                                            fieldWithPath("result[].thumbnailBackground").description("졸업한 스터디 썸네일 배경색"),
                                            fieldWithPath("result[].review").description("작성한 리뷰")
                                                    .optional(),
                                            fieldWithPath("result[].review.id").description("작성한 리뷰 ID")
                                                    .optional(),
                                            fieldWithPath("result[].review.content").description("작성한 리뷰")
                                                    .optional(),
                                            fieldWithPath("result[].review.writtenDate").description("리뷰 작성 날짜")
                                                    .optional(),
                                            fieldWithPath("result[].review.lastModifiedDate").description("리뷰 수정 날짜")
                                                    .optional()
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자가 받은 리뷰 조회 API [GET /api/members/{memberId}/reviews]")
    class getReviews {
        private static final String BASE_URL = "/api/members/{memberId}/reviews";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("사용자가 받은 리뷰를 조회한다")
        void success() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);
            given(queryReceivedReviewByIdUseCase.queryReceivedReview(any()))
                    .willReturn(
                            List.of(
                                    new ReceivedReview("Good Participant", LocalDateTime.now()),
                                    new ReceivedReview("Good Participant", LocalDateTime.now()),
                                    new ReceivedReview("Good Participant", LocalDateTime.now())
                            )
                    );

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Query/Public/ReceivedReview",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].content").description("내용"),
                                            fieldWithPath("result[].writtenDate").description("작성 날짜")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자 출석률 조회 API [GET /api/members/{memberId}/attendances]")
    class getAttendanceRatio {
        private static final String BASE_URL = "/api/members/{memberId}/attendances";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("사용자의 출석률을 조회한다")
        void success() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);
            given(queryAttendanceRatioByIdUseCase.queryAttendanceRatio(any()))
                    .willReturn(
                            List.of(
                                    new AttendanceRatio(ATTENDANCE, 13),
                                    new AttendanceRatio(LATE, 2),
                                    new AttendanceRatio(ABSENCE, 1),
                                    new AttendanceRatio(NON_ATTENDANCE, 5)
                            )
                    );

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Query/Public/AttendanceRatio",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].status").description("출석 상태"),
                                            fieldWithPath("result[].count").description("출석 횟수")
                                    )
                            )
                    );
        }
    }
}
