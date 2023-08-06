package com.kgu.studywithme.study.presentation;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.ReviewInformation;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.StudyBasicInformation;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.StudyMember;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.StudyParticipantInformation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.*;
import static com.kgu.studywithme.common.fixture.StudyFixture.LINE_INTERVIEW;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.*;
import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static com.kgu.studywithme.study.domain.RecruitmentStatus.IN_PROGRESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Study -> StudyInformationApiController 테스트")
class StudyInformationApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("스터디 기본 정보 조회 API [GET /api/studies/{studyId}]")
    class getInformation {
        private static final String BASE_URL = "/api/studies/{studyId}";
        private static final Long STUDY_ID = 1L;

        @Test
        @DisplayName("스터디 기본 정보를 조회한다")
        void success() throws Exception {
            // given
            given(queryBasicInformationByIdUseCase.queryBasicInformation(any())).willReturn(createStudyBasicInformation());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "StudyApi/Query/Public/BasicInformation",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("id")
                                                    .description("스터디 ID(PK)"),
                                            fieldWithPath("name")
                                                    .description("스터디명"),
                                            fieldWithPath("description")
                                                    .description("스터디 설명"),
                                            fieldWithPath("category")
                                                    .description("스터디 카테고리"),
                                            fieldWithPath("thumbnail.name")
                                                    .description("스터디 썸네일 이미지"),
                                            fieldWithPath("thumbnail.background")
                                                    .description("스터디 썸네일 배경색"),
                                            fieldWithPath("type")
                                                    .description("스터디 타입")
                                                    .attributes(constraint("온라인 / 오프라인")),
                                            fieldWithPath("location.province")
                                                    .description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("온라인 스터디 = null")),
                                            fieldWithPath("location.city")
                                                    .description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("온라인 스터디 = null")),
                                            fieldWithPath("recruitmentStatus")
                                                    .description("스터디 모집 여부"),
                                            fieldWithPath("maxMember")
                                                    .description("스터디 최대 인원"),
                                            fieldWithPath("participantMembers")
                                                    .description("현재 스터디 참여자 수"),
                                            fieldWithPath("minimumAttendanceForGraduation")
                                                    .description("스터디 졸업 요건 [최소 출석 횟수]"),
                                            fieldWithPath("remainingOpportunityToUpdateGraduationPolicy")
                                                    .description("남은 졸업 요건 변경 횟수"),
                                            fieldWithPath("host.id")
                                                    .description("스터디 팀장 ID(PK)"),
                                            fieldWithPath("host.nickname")
                                                    .description("스터디 팀장 닉네임"),
                                            fieldWithPath("hashtags[]")
                                                    .description("스터디 해시태그"),
                                            fieldWithPath("participants[].id")
                                                    .description("스터디 참여자 ID(PK)"),
                                            fieldWithPath("participants[].nickname")
                                                    .description("스터디 참여자 닉네임"),
                                            fieldWithPath("participants[].gender")
                                                    .description("스터디 참여자 성별"),
                                            fieldWithPath("participants[].score")
                                                    .description("스터디 참여자 점수"),
                                            fieldWithPath("participants[].age")
                                                    .description("스터디 참여자 나이")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 리뷰 조회 API [GET /api/studies/{studyId}/reviews]")
    class getReviews {
        private static final String BASE_URL = "/api/studies/{studyId}/reviews";
        private static final Long STUDY_ID = 1L;

        @Test
        @DisplayName("스터디 리뷰를 조회한다")
        void success() throws Exception {
            // given
            given(queryReviewByIdUseCase.queryReview(any()))
                    .willReturn(
                            new ReviewInformation(
                                    List.of(
                                            new ReviewInformation.ReviewMetadata(
                                                    2L,
                                                    "팀장님이 잘 이끌어주세요",
                                                    LocalDateTime.now().minusDays(1),
                                                    new StudyMember(1L, JIWON.getNickname())
                                            ),
                                            new ReviewInformation.ReviewMetadata(
                                                    1L,
                                                    "스터디 자료가 좋아요",
                                                    LocalDateTime.now().minusDays(3),
                                                    new StudyMember(2L, GHOST.getNickname())
                                            )
                                    ),
                                    8
                            )
                    );

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "StudyApi/Query/Public/Review",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("reviews[].id")
                                                    .description("리뷰 ID(PK)"),
                                            fieldWithPath("reviews[].content")
                                                    .description("리뷰 내용"),
                                            fieldWithPath("reviews[].writtenDate")
                                                    .description("리뷰 작성 날짜"),
                                            fieldWithPath("reviews[].reviewer.id")
                                                    .description("리뷰어 ID(PK)"),
                                            fieldWithPath("reviews[].reviewer.nickname")
                                                    .description("리뷰어 닉네임"),
                                            fieldWithPath("graduateCount")
                                                    .description("졸업한 사람 수")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 참여자 조회 API [GET /api/studies/{studyId}/participants] - AccessToken 필수")
    class getApproveParticipants {
        private static final String BASE_URL = "/api/studies/{studyId}/participants";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;

        @Test
        @DisplayName("스터디 참여자 정보를 조회한다")
        void success() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            given(queryParticipantByIdUseCase.queryParticipant(any()))
                    .willReturn(
                            new StudyParticipantInformation(
                                    new StudyMember(1L, JIWON.getNickname()),
                                    List.of(
                                            new StudyMember(2L, GHOST.getNickname()),
                                            new StudyMember(3L, DUMMY1.getNickname()),
                                            new StudyMember(4L, DUMMY2.getNickname()),
                                            new StudyMember(5L, DUMMY3.getNickname())
                                    )
                            )
                    );

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "StudyApi/Query/Public/Participant",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("host.id")
                                                    .description("팀장 ID(PK)"),
                                            fieldWithPath("host.nickname")
                                                    .description("팀장 닉네임"),
                                            fieldWithPath("participants[].id")
                                                    .description("참여자 ID(PK)"),
                                            fieldWithPath("participants[].nickname")
                                                    .description("참여자 닉네임")
                                    )
                            )
                    );
        }
    }

    private StudyBasicInformation createStudyBasicInformation() {
        return new StudyBasicInformation(
                1L,
                LINE_INTERVIEW.getName().getValue(),
                LINE_INTERVIEW.getDescription().getValue(),
                LINE_INTERVIEW.getCategory().getName(),
                new StudyBasicInformation.Thumbnail(
                        LINE_INTERVIEW.getThumbnail().getImageName(),
                        LINE_INTERVIEW.getThumbnail().getBackground()
                ),
                LINE_INTERVIEW.getType(),
                LINE_INTERVIEW.getLocation(),
                IN_PROGRESS,
                LINE_INTERVIEW.getCapacity().getValue(),
                LINE_INTERVIEW.getCapacity().getValue() - 2,
                LINE_INTERVIEW.getMinimumAttendanceForGraduation(),
                3,
                new StudyMember(1L, JIWON.getNickname()),
                new ArrayList<>(LINE_INTERVIEW.getHashtags()),
                List.of(
                        new StudyBasicInformation.ParticipantInformation(
                                1L,
                                JIWON.getNickname().getValue(),
                                JIWON.getGender().getValue(),
                                98,
                                22
                        ),
                        new StudyBasicInformation.ParticipantInformation(
                                2L,
                                GHOST.getNickname().getValue(),
                                GHOST.getGender().getValue(),
                                85,
                                23
                        ),
                        new StudyBasicInformation.ParticipantInformation(
                                3L,
                                DUMMY1.getNickname().getValue(),
                                DUMMY1.getGender().getValue(),
                                92,
                                28
                        ),
                        new StudyBasicInformation.ParticipantInformation(
                                4L,
                                DUMMY2.getNickname().getValue(),
                                DUMMY2.getGender().getValue(),
                                78,
                                26
                        )
                )
        );
    }
}
