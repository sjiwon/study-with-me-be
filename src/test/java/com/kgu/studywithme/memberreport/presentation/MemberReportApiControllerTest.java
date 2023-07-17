package com.kgu.studywithme.memberreport.presentation;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.memberreport.exception.MemberReportErrorCode;
import com.kgu.studywithme.memberreport.presentation.dto.request.ReportMemberRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("MemberReport -> MemberReportApiController 테스트")
class MemberReportApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("사용자 신고 API [POST /api/members/{reporteeId}/report] - AccessToken 필수")
    class report {
        private static final String BASE_URL = "/api/members/{reporteeId}/report";
        private static final Long REPORTEE_ID = 1L;
        private static final Long REPORTER_ID = 2L;

        @Test
        @DisplayName("이전에 신고한 내역이 처리되지 않고 접수상태로 남아있다면 중복 신고를 하지 못한다")
        void throwExceptionByPreviousReportIsStillPending() throws Exception {
            // given
            mockingToken(true, REPORTER_ID);
            doThrow(StudyWithMeException.type(MemberReportErrorCode.PREVIOUS_REPORT_IS_STILL_PENDING))
                    .when(reportMemberUseCase)
                    .report(any());

            // when
            final ReportMemberRequest request = new ReportMemberRequest("참여를 안해요");
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, REPORTEE_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final MemberReportErrorCode expectedError = MemberReportErrorCode.PREVIOUS_REPORT_IS_STILL_PENDING;
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
                                    "MemberApi/Report/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("reporteeId").description("신고 대상자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("reason").description("신고 사유")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("사용자 신고에 성공한다")
        void success() throws Exception {
            // given
            mockingToken(true, REPORTER_ID);
            given(reportMemberUseCase.report(any())).willReturn(1L);

            // when
            final ReportMemberRequest request = new ReportMemberRequest("참여를 안해요");
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, REPORTEE_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "MemberApi/Report/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("reporteeId").description("신고 대상자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("reason").description("신고 사유")
                                    )
                            )
                    );
        }
    }
}
