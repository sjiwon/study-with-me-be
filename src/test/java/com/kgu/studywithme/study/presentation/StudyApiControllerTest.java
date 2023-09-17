package com.kgu.studywithme.study.presentation;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.study.presentation.dto.request.CreateStudyRequest;
import com.kgu.studywithme.study.presentation.dto.request.UpdateStudyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.kgu.studywithme.common.fixture.StudyFixture.REAL_MYSQL;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.constraint;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentRequest;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentResponse;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getExceptionResponseFields;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getHeaderWithAccessToken;
import static com.kgu.studywithme.common.utils.TokenUtils.applyAccessTokenToAuthorizationHeader;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Study -> StudyApiController 테스트")
class StudyApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("스터디 생성 API [POST /api/studies] - AccessToken 필수")
    class Create {
        private static final String BASE_URL = "/api/studies";
        private static final Long HOST_ID = 1L;
        private static final CreateStudyRequest ONLINE_STUDY_REQUEST = new CreateStudyRequest(
                SPRING.getName().getValue(),
                SPRING.getDescription().getValue(),
                SPRING.getCapacity().getValue(),
                SPRING.getCategory().getId(),
                SPRING.getThumbnail().getImageName(),
                SPRING.getType().getValue(),
                null,
                null,
                SPRING.getMinimumAttendanceForGraduation(),
                SPRING.getHashtags()
        );
        private static final CreateStudyRequest OFFLINE_STUDY_REQUEST = new CreateStudyRequest(
                REAL_MYSQL.getName().getValue(),
                REAL_MYSQL.getDescription().getValue(),
                REAL_MYSQL.getCapacity().getValue(),
                REAL_MYSQL.getCategory().getId(),
                REAL_MYSQL.getThumbnail().getImageName(),
                REAL_MYSQL.getType().getValue(),
                REAL_MYSQL.getLocation().getProvince(),
                REAL_MYSQL.getLocation().getCity(),
                REAL_MYSQL.getMinimumAttendanceForGraduation(),
                REAL_MYSQL.getHashtags()
        );

        @Test
        @DisplayName("이미 사용하고 있는 이름이면 스터디 생성에 실패한다")
        void throwExceptionByDuplicateName() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.DUPLICATE_NAME))
                    .when(createStudyUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader())
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(ONLINE_STUDY_REQUEST));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.DUPLICATE_NAME;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isConflict())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Create/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    requestFields(
                                            fieldWithPath("name")
                                                    .description("스터디명"),
                                            fieldWithPath("description")
                                                    .description("스터디 설명"),
                                            fieldWithPath("category")
                                                    .description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail")
                                                    .description("스터디 썸네일"),
                                            fieldWithPath("capacity")
                                                    .description("최대 수용 인원"),
                                            fieldWithPath("type")
                                                    .description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = online / 오프라인 = offline")),
                                            fieldWithPath("province")
                                                    .description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city")
                                                    .description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("minimumAttendanceForGraduation")
                                                    .description("졸업 요건 [최소 출석 횟수]"),
                                            fieldWithPath("hashtags")
                                                    .description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("스터디를 생성한다 - 온라인")
        void successOnline() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            given(createStudyUseCase.invoke(any())).willReturn(1L);

            // when
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader())
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(ONLINE_STUDY_REQUEST));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isCreated(),
                            jsonPath("$.studyId").value(1L)
                    )
                    .andDo(
                            document(
                                    "StudyApi/Create/Success/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    requestFields(
                                            fieldWithPath("name")
                                                    .description("스터디명"),
                                            fieldWithPath("description")
                                                    .description("스터디 설명"),
                                            fieldWithPath("category")
                                                    .description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail")
                                                    .description("스터디 썸네일"),
                                            fieldWithPath("capacity")
                                                    .description("최대 수용 인원"),
                                            fieldWithPath("type")
                                                    .description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = online / 오프라인 = offline")),
                                            fieldWithPath("province")
                                                    .description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city")
                                                    .description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("minimumAttendanceForGraduation")
                                                    .description("졸업 요건 [최소 출석 횟수]"),
                                            fieldWithPath("hashtags")
                                                    .description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    ),
                                    responseFields(
                                            fieldWithPath("studyId")
                                                    .description("생성한 스터디 ID(PK)")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("스터디를 생성한다 - 오프라인")
        void successOffline() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            given(createStudyUseCase.invoke(any())).willReturn(1L);

            // when
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader())
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(OFFLINE_STUDY_REQUEST));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isCreated(),
                            jsonPath("$.studyId").value(1L)
                    )
                    .andDo(
                            document(
                                    "StudyApi/Create/Success/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    requestFields(
                                            fieldWithPath("name")
                                                    .description("스터디명"),
                                            fieldWithPath("description")
                                                    .description("스터디 설명"),
                                            fieldWithPath("category")
                                                    .description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail")
                                                    .description("스터디 썸네일"),
                                            fieldWithPath("capacity")
                                                    .description("최대 수용 인원"),
                                            fieldWithPath("type")
                                                    .description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = online / 오프라인 = offline")),
                                            fieldWithPath("province")
                                                    .description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city")
                                                    .description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("minimumAttendanceForGraduation")
                                                    .description("졸업 요건 [최소 출석 횟수]"),
                                            fieldWithPath("hashtags")
                                                    .description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    ),
                                    responseFields(
                                            fieldWithPath("studyId")
                                                    .description("생성한 스터디 ID(PK)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 정보 수정 API [PATCH /api/studies/{studyId}] - AccessToken 필수")
    class Update {
        private static final String BASE_URL = "/api/studies/{studyId}";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;
        private static final UpdateStudyRequest ONLINE_STUDY_REQUEST = new UpdateStudyRequest(
                SPRING.getName().getValue(),
                SPRING.getDescription().getValue(),
                SPRING.getCapacity().getValue(),
                SPRING.getCategory().getId(),
                SPRING.getThumbnail().getImageName(),
                SPRING.getType().getValue(),
                null,
                null,
                true,
                SPRING.getMinimumAttendanceForGraduation(),
                SPRING.getHashtags()
        );
        private static final UpdateStudyRequest OFFLINE_STUDY_REQUEST = new UpdateStudyRequest(
                REAL_MYSQL.getName().getValue(),
                REAL_MYSQL.getDescription().getValue(),
                REAL_MYSQL.getCapacity().getValue(),
                REAL_MYSQL.getCategory().getId(),
                REAL_MYSQL.getThumbnail().getImageName(),
                REAL_MYSQL.getType().getValue(),
                REAL_MYSQL.getLocation().getProvince(),
                REAL_MYSQL.getLocation().getCity(),
                true,
                REAL_MYSQL.getMinimumAttendanceForGraduation(),
                REAL_MYSQL.getHashtags()
        );

        @BeforeEach
        void setUp() {
            mockingForStudyHost(STUDY_ID, HOST_ID);
        }

        @Test
        @DisplayName("스터디 팀장이 아니라면 정보를 수정할 수 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            mockingToken(true, ANONYMOUS_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader())
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(ONLINE_STUDY_REQUEST));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_NOT_HOST;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Update/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("name")
                                                    .description("스터디명"),
                                            fieldWithPath("description")
                                                    .description("스터디 설명"),
                                            fieldWithPath("capacity")
                                                    .description("최대 수용 인원"),
                                            fieldWithPath("category")
                                                    .description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail")
                                                    .description("스터디 썸네일"),
                                            fieldWithPath("type")
                                                    .description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = online / 오프라인 = offline")),
                                            fieldWithPath("province")
                                                    .description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city")
                                                    .description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus")
                                                    .description("스터디 모집 활성화 여부")
                                                    .attributes(constraint("활성화=true / 비활성화=false")),
                                            fieldWithPath("minimumAttendanceForGraduation")
                                                    .description("졸업 요건 [최소 출석 횟수]"),
                                            fieldWithPath("hashtags")
                                                    .description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("다른 스터디가 사용하고 있는 이름으로 수정할 수 없다")
        void throwExceptionByDuplicateName() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.DUPLICATE_NAME))
                    .when(updateStudyUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader())
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(ONLINE_STUDY_REQUEST));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.DUPLICATE_NAME;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isConflict())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Update/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("name")
                                                    .description("스터디명"),
                                            fieldWithPath("description")
                                                    .description("스터디 설명"),
                                            fieldWithPath("capacity")
                                                    .description("최대 수용 인원"),
                                            fieldWithPath("category")
                                                    .description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail")
                                                    .description("스터디 썸네일"),
                                            fieldWithPath("type")
                                                    .description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = online / 오프라인 = offline")),
                                            fieldWithPath("province")
                                                    .description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city")
                                                    .description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus")
                                                    .description("스터디 모집 활성화 여부")
                                                    .attributes(constraint("활성화=true / 비활성화=false")),
                                            fieldWithPath("minimumAttendanceForGraduation")
                                                    .description("졸업 요건 [최소 출석 횟수]"),
                                            fieldWithPath("hashtags")
                                                    .description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("현재 참여자 수보다 낮게 스터디 정원을 수정할 수 없다")
        void throwExceptionByCapacityCannotCoverCurrentParticipants() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.CAPACITY_CANNOT_COVER_CURRENT_PARTICIPANTS))
                    .when(updateStudyUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader())
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(ONLINE_STUDY_REQUEST));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.CAPACITY_CANNOT_COVER_CURRENT_PARTICIPANTS;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isConflict())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Update/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("name")
                                                    .description("스터디명"),
                                            fieldWithPath("description")
                                                    .description("스터디 설명"),
                                            fieldWithPath("capacity")
                                                    .description("최대 수용 인원"),
                                            fieldWithPath("category")
                                                    .description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail")
                                                    .description("스터디 썸네일"),
                                            fieldWithPath("type")
                                                    .description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = online / 오프라인 = offline")),
                                            fieldWithPath("province")
                                                    .description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city")
                                                    .description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus")
                                                    .description("스터디 모집 활성화 여부")
                                                    .attributes(constraint("활성화=true / 비활성화=false")),
                                            fieldWithPath("minimumAttendanceForGraduation")
                                                    .description("졸업 요건 [최소 출석 횟수]"),
                                            fieldWithPath("hashtags")
                                                    .description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("졸업 요건 수정 기회가 남아있지 않음에 따라 스터디 정보를 수정할 수 없다")
        void throwExceptionByNoChanceToUpdateGraduationPolicy() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doThrow(StudyWithMeException.type(StudyErrorCode.NO_CHANCE_TO_UPDATE_GRADUATION_POLICY))
                    .when(updateStudyUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader())
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(ONLINE_STUDY_REQUEST));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.NO_CHANCE_TO_UPDATE_GRADUATION_POLICY;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isConflict())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Update/Failure/Case4",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("name")
                                                    .description("스터디명"),
                                            fieldWithPath("description")
                                                    .description("스터디 설명"),
                                            fieldWithPath("capacity")
                                                    .description("최대 수용 인원"),
                                            fieldWithPath("category")
                                                    .description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail")
                                                    .description("스터디 썸네일"),
                                            fieldWithPath("type")
                                                    .description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = online / 오프라인 = offline")),
                                            fieldWithPath("province")
                                                    .description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city")
                                                    .description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus")
                                                    .description("스터디 모집 활성화 여부")
                                                    .attributes(constraint("활성화=true / 비활성화=false")),
                                            fieldWithPath("minimumAttendanceForGraduation")
                                                    .description("졸업 요건 [최소 출석 횟수]"),
                                            fieldWithPath("hashtags")
                                                    .description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 정보를 수정한다 - 온라인")
        void successOnline() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doNothing()
                    .when(updateStudyUseCase)
                    .invoke(any());

            // when
            final UpdateStudyRequest request = new UpdateStudyRequest(
                    SPRING.getName().getValue(),
                    SPRING.getDescription().getValue(),
                    SPRING.getCapacity().getValue(),
                    SPRING.getCategory().getId(),
                    SPRING.getThumbnail().getImageName(),
                    SPRING.getType().getValue(),
                    null,
                    null,
                    true,
                    SPRING.getMinimumAttendanceForGraduation(),
                    SPRING.getHashtags()
            );
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader())
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Update/Success/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("name")
                                                    .description("스터디명"),
                                            fieldWithPath("description")
                                                    .description("스터디 설명"),
                                            fieldWithPath("capacity")
                                                    .description("최대 수용 인원"),
                                            fieldWithPath("category")
                                                    .description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail")
                                                    .description("스터디 썸네일"),
                                            fieldWithPath("type")
                                                    .description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = online / 오프라인 = offline")),
                                            fieldWithPath("province")
                                                    .description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city")
                                                    .description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus")
                                                    .description("스터디 모집 활성화 여부")
                                                    .attributes(constraint("활성화=true / 비활성화=false")),
                                            fieldWithPath("minimumAttendanceForGraduation")
                                                    .description("졸업 요건 [최소 출석 횟수]"),
                                            fieldWithPath("hashtags")
                                                    .description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("스터디 정보를 수정한다 - 오프라인")
        void successOffline() throws Exception {
            // given
            mockingToken(true, HOST_ID);
            doNothing()
                    .when(updateStudyUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader())
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(OFFLINE_STUDY_REQUEST));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Update/Success/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("name")
                                                    .description("스터디명"),
                                            fieldWithPath("description")
                                                    .description("스터디 설명"),
                                            fieldWithPath("capacity")
                                                    .description("최대 수용 인원"),
                                            fieldWithPath("category")
                                                    .description("카테고리 ID(PK)"),
                                            fieldWithPath("thumbnail")
                                                    .description("스터디 썸네일"),
                                            fieldWithPath("type")
                                                    .description("온/오프라인 유무")
                                                    .attributes(constraint("온라인 = online / 오프라인 = offline")),
                                            fieldWithPath("province")
                                                    .description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("city")
                                                    .description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("오프라인 스터디의 경우 필수")),
                                            fieldWithPath("recruitmentStatus")
                                                    .description("스터디 모집 활성화 여부")
                                                    .attributes(constraint("활성화=true / 비활성화=false")),
                                            fieldWithPath("minimumAttendanceForGraduation")
                                                    .description("졸업 요건 [최소 출석 횟수]"),
                                            fieldWithPath("hashtags")
                                                    .description("해시태그")
                                                    .attributes(constraint("최소 1개 최대 5개"))
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 종료 API [DELETE /api/studies/{studyId}] - AccessToken 필수")
    class Terminate {
        private static final String BASE_URL = "/api/studies/{studyId}";
        private static final Long STUDY_ID = 1L;
        private static final Long HOST_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @BeforeEach
        void setUp() {
            mockingForStudyHost(STUDY_ID, HOST_ID);
        }

        @Test
        @DisplayName("스터디 팀장이 아니라면 스터디를 종료할 수 없다")
        void throwExceptionByMemberIsNotHost() throws Exception {
            // given
            mockingToken(true, ANONYMOUS_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader());

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_NOT_HOST;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "StudyApi/Terminate/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("스터디를 종료한다")
        void success() throws Exception {
            // given
            mockingToken(true, HOST_ID);

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, STUDY_ID)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader());

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Terminate/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("studyId")
                                                    .description("스터디 ID(PK)")
                                    )
                            )
                    );
        }
    }
}
