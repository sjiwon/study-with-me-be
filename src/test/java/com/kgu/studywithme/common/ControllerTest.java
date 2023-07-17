package com.kgu.studywithme.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgu.studywithme.auth.application.usecase.command.LogoutUseCase;
import com.kgu.studywithme.auth.application.usecase.command.OAuthLoginUseCase;
import com.kgu.studywithme.auth.application.usecase.command.TokenReissueUseCase;
import com.kgu.studywithme.auth.application.usecase.query.QueryOAuthLinkUseCase;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.presentation.OAuthApiController;
import com.kgu.studywithme.auth.presentation.TokenReissueApiController;
import com.kgu.studywithme.auth.utils.JwtTokenProvider;
import com.kgu.studywithme.category.application.usecase.query.QueryAllCategoriesUseCase;
import com.kgu.studywithme.category.presentation.CategoryApiController;
import com.kgu.studywithme.common.config.TestAopConfiguration;
import com.kgu.studywithme.favorite.application.usecase.command.StudyLikeCancellationUseCase;
import com.kgu.studywithme.favorite.application.usecase.command.StudyLikeMarkingUseCase;
import com.kgu.studywithme.favorite.presentation.FavoriteApiController;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.MemberInformationService;
import com.kgu.studywithme.member.application.usecase.command.SignUpMemberUseCase;
import com.kgu.studywithme.member.application.usecase.command.UpdateMemberUseCase;
import com.kgu.studywithme.member.presentation.MemberApiController;
import com.kgu.studywithme.member.presentation.MemberInformationApiController;
import com.kgu.studywithme.peerreview.application.usecase.command.UpdatePeerReviewUseCase;
import com.kgu.studywithme.peerreview.application.usecase.command.WritePeerReviewUseCase;
import com.kgu.studywithme.peerreview.presentation.MemberReviewApiController;
import com.kgu.studywithme.report.application.usecase.command.ReportMemberUseCase;
import com.kgu.studywithme.report.presentation.MemberReportApiController;
import com.kgu.studywithme.study.application.*;
import com.kgu.studywithme.study.application.attendance.AttendanceService;
import com.kgu.studywithme.study.application.notice.NoticeCommentService;
import com.kgu.studywithme.study.application.notice.NoticeService;
import com.kgu.studywithme.study.application.week.StudyWeeklyService;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.study.presentation.*;
import com.kgu.studywithme.study.presentation.attendance.AttendanceApiController;
import com.kgu.studywithme.study.presentation.notice.StudyNoticeApiController;
import com.kgu.studywithme.study.presentation.notice.StudyNoticeCommentApiController;
import com.kgu.studywithme.study.presentation.week.StudyWeeklyApiController;
import com.kgu.studywithme.upload.application.usecase.command.UploadStudyDescriptionImageUseCase;
import com.kgu.studywithme.upload.application.usecase.command.UploadWeeklyImageUseCase;
import com.kgu.studywithme.upload.presentation.UploadApiController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest({
        // Auth
        OAuthApiController.class, TokenReissueApiController.class,

        // Category & Favorite
        CategoryApiController.class, FavoriteApiController.class,

        // Member
        MemberApiController.class, MemberInformationApiController.class,

        // Review
        MemberReviewApiController.class,

        // Report
        MemberReportApiController.class,

        // Study
        StudyApiController.class, StudyInformationApiController.class, StudyParticipationApiController.class,
        StudyReviewApiController.class, StudySearchApiController.class,
        StudyNoticeApiController.class, StudyNoticeCommentApiController.class,
        AttendanceApiController.class, StudyWeeklyApiController.class,

        // Upload
        UploadApiController.class,
})
@ExtendWith(RestDocumentationExtension.class)
@Import(TestAopConfiguration.class)
@AutoConfigureRestDocs
public abstract class ControllerTest {
    // common & external
    @Autowired
    protected MockMvc mockMvc;

    // common & internal
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private StudyValidator studyValidator;

    // Auth
    @MockBean
    protected QueryOAuthLinkUseCase queryOAuthLinkUseCase;

    @MockBean
    protected OAuthLoginUseCase oAuthLoginUseCase;

    @MockBean
    protected LogoutUseCase logoutUseCase;

    @MockBean
    protected TokenReissueUseCase tokenReissueUseCase;

    // Category & Favorite
    @MockBean
    protected QueryAllCategoriesUseCase queryAllCategoriesUseCase;

    @MockBean
    protected StudyLikeMarkingUseCase studyLikeMarkingUseCase;

    @MockBean
    protected StudyLikeCancellationUseCase studyLikeCancellationUseCase;

    // Member
    @MockBean
    protected SignUpMemberUseCase signUpMemberUseCase;

    @MockBean
    protected UpdateMemberUseCase updateMemberUseCase;

    @MockBean
    protected MemberInformationService memberInformationService;

    // PeerReview
    @MockBean
    protected WritePeerReviewUseCase writePeerReviewUseCase;

    @MockBean
    protected UpdatePeerReviewUseCase updatePeerReviewUseCase;

    // Report
    @MockBean
    protected ReportMemberUseCase reportMemberUseCase;

    // Study
    @MockBean
    protected StudyService studyService;

    @MockBean
    protected StudyInformationService studyInformationService;

    @MockBean
    protected ParticipationService participationService;

    @MockBean
    protected StudyReviewService studyReviewService;

    @MockBean
    protected StudySearchService studySearchService;

    @MockBean
    protected NoticeService noticeService;

    @MockBean
    protected NoticeCommentService commentService;

    @MockBean
    protected AttendanceService attendanceService;

    @MockBean
    protected StudyWeeklyService studyWeeklyService;

    // Upload
    @MockBean
    protected UploadWeeklyImageUseCase uploadWeeklyImageUseCase;

    @MockBean
    protected UploadStudyDescriptionImageUseCase uploadStudyDescriptionImageUseCase;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(print())
                .alwaysDo(log())
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    protected OperationRequestPreprocessor getDocumentRequest() {
        return preprocessRequest(prettyPrint());
    }

    protected OperationResponsePreprocessor getDocumentResponse() {
        return preprocessResponse(prettyPrint());
    }

    protected Snippet getHeaderWithAccessToken() {
        return requestHeaders(
                headerWithName(AUTHORIZATION).description("Access Token")
        );
    }

    protected Snippet getHeaderWithRefreshToken() {
        return requestHeaders(
                headerWithName(AUTHORIZATION).description("Refresh Token")
        );
    }

    protected Snippet getExceptionResponseFiels() {
        return responseFields(
                fieldWithPath("status").description("HTTP 상태 코드"),
                fieldWithPath("errorCode").description("커스텀 예외 코드"),
                fieldWithPath("message").description("예외 메시지")
        );
    }

    protected Attributes.Attribute constraint(String value) {
        return new Attributes.Attribute("constraints", value);
    }

    protected String convertObjectToJson(Object data) throws JsonProcessingException {
        return objectMapper.writeValueAsString(data);
    }

    protected void mockingToken(boolean isValid, Long payloadId) {
        given(jwtTokenProvider.isTokenValid(anyString())).willReturn(isValid);
        given(jwtTokenProvider.getId(anyString())).willReturn(payloadId);
    }

    protected void mockingTokenWithExpiredException() {
        doThrow(StudyWithMeException.type(AuthErrorCode.AUTH_EXPIRED_TOKEN))
                .when(jwtTokenProvider)
                .isTokenValid(any());
    }

    protected void mockingTokenWithInvalidException() {
        doThrow(StudyWithMeException.type(AuthErrorCode.AUTH_INVALID_TOKEN))
                .when(jwtTokenProvider)
                .isTokenValid(any());
    }

    protected void mockingForStudyParticipant(Long studyId, Long memberId, boolean isValid) {
        if (isValid) {
            doNothing()
                    .when(studyValidator)
                    .validateStudyParticipant(studyId, memberId);
        } else {
            doThrow(StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_PARTICIPANT))
                    .when(studyValidator)
                    .validateStudyParticipant(studyId, memberId);
        }
    }

    protected void mockingForStudyHost(Long studyId, Long memberId, boolean isValid) {
        if (isValid) {
            doNothing()
                    .when(studyValidator)
                    .validateHost(studyId, memberId);
        } else {
            doThrow(StudyWithMeException.type(StudyErrorCode.MEMBER_IS_NOT_HOST))
                    .when(studyValidator)
                    .validateHost(studyId, memberId);
        }
    }
}
