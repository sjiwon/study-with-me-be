package com.kgu.studywithme.study.controller.attendance;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.fixture.MemberFixture;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.controller.dto.request.AttendanceRequest;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.ATTENDANCE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Study [Controller Layer] -> AttendanceApiController 테스트")
class AttendanceApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("수동 출석 API [PATCH /api/studies/{studyId}/attendance/{memberId}]")
    class manualCheckAttendance {
        private static final String BASE_URL = "/api/studies/{studyId}/attendance/{memberId}";
        private static final Long STUDY_ID = 1L;
        private static final Long PARTICIPANT_ID = 2L;
        private static final Long ANONYMOUS_ID = 3L;

        @BeforeEach
        void setUp() {
            Member host = createMember(JIWON, 1L);
            Member memberA = createMember(DUMMY1, 2L);
            Member memberB = createMember(DUMMY2, 2L);
            given(memberFindService.findById(1L)).willReturn(host);
            given(memberFindService.findById(2L)).willReturn(memberA);
            given(memberFindService.findById(3L)).willReturn(memberB);

            Study study = createSpringStudy(host, 1L);
            given(studyFindService.findById(1L)).willReturn(study);

            study.applyParticipation(memberA);
            study.approveParticipation(memberA);
        }

        private Member createMember(MemberFixture fixture, Long id) {
            Member member = fixture.toMember();
            ReflectionTestUtils.setField(member, "id", id);
            return member;
        }

        private Study createSpringStudy(Member host, Long id) {
            Study study = SPRING.toOnlineStudy(host);
            ReflectionTestUtils.setField(study, "id", id);
            return study;
        }

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 수동 출석을 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            final AttendanceRequest request = createAttendanceRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, PARTICIPANT_ID)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_PERMISSION;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isForbidden(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "StudyApi/Attendance/ManualCheck/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("studyId").description("수동 출석 체크할 스터디 ID(PK)"),
                                            parameterWithName("memberId").description("수동 출석 체크할 참여자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("week").description("수동 출석할 주차"),
                                            fieldWithPath("status").description("출석 정보")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("스터디 참여자가 아니면 출석 체크를 할 수 없다")
        void throwExceptionByAnonymousMember() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);

            // when
            final AttendanceRequest request = createAttendanceRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, ANONYMOUS_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_NOT_PARTICIPANT;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isConflict(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "StudyApi/Attendance/ManualCheck/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("studyId").description("수동 출석 체크할 스터디 ID(PK)"),
                                            parameterWithName("memberId").description("수동 출석 체크할 참여자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("week").description("수동 출석할 주차"),
                                            fieldWithPath("status").description("출석 정보")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("팀장이 아니라면 수동으로 출석 정보를 변경할 수 없다")
        void throwExceptionByMemberNotHost() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);
            doThrow(StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_HOST))
                    .when(attendanceService)
                    .manualCheckAttendance(any(), any(), any(), any(), any());

            // when
            final AttendanceRequest request = createAttendanceRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, PARTICIPANT_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.MEMBER_IS_NOT_HOST;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isConflict(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "StudyApi/Attendance/ManualCheck/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("studyId").description("수동 출석 체크할 스터디 ID(PK)"),
                                            parameterWithName("memberId").description("수동 출석 체크할 참여자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("week").description("수동 출석할 주차"),
                                            fieldWithPath("status").description("출석 정보")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("해당 사용자의 출석 정보가 존재하지 않는다")
        void throwExceptionByAttendanceNotFound() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);
            doThrow(StudyWithMeException.type(StudyErrorCode.ATTENDANCE_NOT_FOUND))
                    .when(attendanceService)
                    .manualCheckAttendance(any(), any(), any(), any(), any());
            // when
            final AttendanceRequest request = createAttendanceRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, PARTICIPANT_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final StudyErrorCode expectedError = StudyErrorCode.ATTENDANCE_NOT_FOUND;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "StudyApi/Attendance/ManualCheck/Failure/Case4",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("studyId").description("수동 출석 체크할 스터디 ID(PK)"),
                                            parameterWithName("memberId").description("수동 출석 체크할 참여자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("week").description("수동 출석할 주차"),
                                            fieldWithPath("status").description("출석 정보")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("수동 출석 체크에 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(1L);
            doNothing()
                    .when(attendanceService)
                    .manualCheckAttendance(any(), any(), any(), any(), any());

            // when
            final AttendanceRequest request = createAttendanceRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, STUDY_ID, PARTICIPANT_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "StudyApi/Attendance/ManualCheck/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("studyId").description("수동 출석 체크할 스터디 ID(PK)"),
                                            parameterWithName("memberId").description("수동 출석 체크할 참여자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("week").description("수동 출석할 주차"),
                                            fieldWithPath("status").description("출석 정보")
                                    )
                            )
                    );
        }
    }

    private AttendanceRequest createAttendanceRequest() {
        return new AttendanceRequest(1, ATTENDANCE.getDescription());
    }
}