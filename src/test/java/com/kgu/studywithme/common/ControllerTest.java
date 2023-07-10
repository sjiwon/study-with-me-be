package com.kgu.studywithme.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgu.studywithme.auth.application.OAuthService;
import com.kgu.studywithme.auth.application.TokenReissueService;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUri;
import com.kgu.studywithme.auth.presentation.OAuthApiController;
import com.kgu.studywithme.auth.presentation.TokenReissueApiController;
import com.kgu.studywithme.auth.utils.JwtTokenProvider;
import com.kgu.studywithme.category.application.CategoryService;
import com.kgu.studywithme.category.presentation.CategoryApiController;
import com.kgu.studywithme.common.config.TestAopConfiguration;
import com.kgu.studywithme.favorite.application.FavoriteManageService;
import com.kgu.studywithme.favorite.presentation.FavoriteApiController;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.MemberInformationService;
import com.kgu.studywithme.member.application.MemberReviewService;
import com.kgu.studywithme.member.application.MemberService;
import com.kgu.studywithme.member.presentation.MemberApiController;
import com.kgu.studywithme.member.presentation.MemberInformationApiController;
import com.kgu.studywithme.member.presentation.MemberReviewApiController;
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
import com.kgu.studywithme.upload.presentation.UploadApiController;
import com.kgu.studywithme.upload.utils.FileUploader;
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
        // auth
        OAuthApiController.class, TokenReissueApiController.class,

        // category & favorite
        CategoryApiController.class, FavoriteApiController.class,

        // member
        MemberApiController.class, MemberInformationApiController.class, MemberReviewApiController.class,

        // study
        StudyApiController.class, StudyInformationApiController.class, StudyParticipationApiController.class,
        StudyReviewApiController.class, StudySearchApiController.class,
        StudyNoticeApiController.class, StudyNoticeCommentApiController.class,
        AttendanceApiController.class, StudyWeeklyApiController.class,

        // upload
        UploadApiController.class,
})
@ExtendWith(RestDocumentationExtension.class)
@Import(TestAopConfiguration.class)
@AutoConfigureRestDocs
public abstract class ControllerTest {
    // common & external
    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    // common & internal
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudyValidator studyValidator;

    // auth
    @MockBean
    protected OAuthUri oAuthUri;

    @MockBean
    protected OAuthService oAuthService;

    @MockBean
    protected TokenReissueService tokenReissueService;

    // category & favorite
    @MockBean
    protected CategoryService categoryService;

    @MockBean
    protected FavoriteManageService favoriteManageService;

    // member
    @MockBean
    protected MemberService memberService;

    @MockBean
    protected MemberInformationService memberInformationService;

    @MockBean
    protected MemberReviewService memberReviewService;

    // study
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

    // upload
    @MockBean
    protected FileUploader uploader;

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
