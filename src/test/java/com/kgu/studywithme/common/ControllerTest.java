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
import com.kgu.studywithme.member.application.usecase.command.SignUpMemberUseCase;
import com.kgu.studywithme.member.application.usecase.command.UpdateMemberUseCase;
import com.kgu.studywithme.member.application.usecase.query.*;
import com.kgu.studywithme.member.presentation.MemberApiController;
import com.kgu.studywithme.member.presentation.MemberPrivateInformationApiController;
import com.kgu.studywithme.member.presentation.MemberPublicInformationApiController;
import com.kgu.studywithme.memberreport.application.usecase.command.ReportMemberUseCase;
import com.kgu.studywithme.memberreport.presentation.MemberReportApiController;
import com.kgu.studywithme.memberreview.application.usecase.command.UpdateMemberReviewUseCase;
import com.kgu.studywithme.memberreview.application.usecase.command.WriteMemberReviewUseCase;
import com.kgu.studywithme.memberreview.presentation.MemberReviewApiController;
import com.kgu.studywithme.study.application.StudyInformationService;
import com.kgu.studywithme.study.application.StudySearchService;
import com.kgu.studywithme.study.application.usecase.command.CreateStudyUseCase;
import com.kgu.studywithme.study.application.usecase.command.TerminateStudyUseCase;
import com.kgu.studywithme.study.application.usecase.command.UpdateStudyUseCase;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.presentation.StudyApiController;
import com.kgu.studywithme.study.presentation.StudyInformationApiController;
import com.kgu.studywithme.study.presentation.StudySearchApiController;
import com.kgu.studywithme.studyattendance.application.usecase.command.ManualAttendanceUseCase;
import com.kgu.studywithme.studyattendance.presentation.StudyAttendanceApiController;
import com.kgu.studywithme.studynotice.application.usecase.command.*;
import com.kgu.studywithme.studynotice.presentation.StudyNoticeApiController;
import com.kgu.studywithme.studynotice.presentation.StudyNoticeCommentApiController;
import com.kgu.studywithme.studyparticipant.application.usecase.command.*;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.presentation.DelegateHostAuthorityApiController;
import com.kgu.studywithme.studyparticipant.presentation.StudyApplyApiController;
import com.kgu.studywithme.studyparticipant.presentation.StudyFinalizeApiController;
import com.kgu.studywithme.studyparticipant.presentation.StudyParticipantDecisionApiController;
import com.kgu.studywithme.studyreview.application.usecase.command.DeleteStudyReviewUseCase;
import com.kgu.studywithme.studyreview.application.usecase.command.UpdateStudyReviewUseCase;
import com.kgu.studywithme.studyreview.application.usecase.command.WriteStudyReviewUseCase;
import com.kgu.studywithme.studyreview.presentation.StudyReviewApiController;
import com.kgu.studywithme.studyweekly.application.usecase.command.*;
import com.kgu.studywithme.studyweekly.presentation.StudyWeeklyApiController;
import com.kgu.studywithme.studyweekly.presentation.StudyWeeklySubmitApiController;
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
        OAuthApiController.class,
        TokenReissueApiController.class,

        // Category
        CategoryApiController.class,

        // Favorite
        FavoriteApiController.class,

        // Member [Command]
        MemberApiController.class,

        // Member [Query]
        MemberPublicInformationApiController.class,
        MemberPrivateInformationApiController.class,

        // MemberReview
        MemberReviewApiController.class,

        // MemberReport
        MemberReportApiController.class,

        // Study [Command]
        StudyApiController.class,

        // Study [Query]
        StudyInformationApiController.class,
        StudySearchApiController.class,

        // StudyParticipant
        StudyApplyApiController.class,
        StudyParticipantDecisionApiController.class,
        DelegateHostAuthorityApiController.class,
        StudyFinalizeApiController.class,

        // StudyAttendance
        StudyAttendanceApiController.class,

        // StudyNotice & StudyNoticeComment
        StudyNoticeApiController.class,
        StudyNoticeCommentApiController.class,

        // StudyWeekly
        StudyWeeklyApiController.class,
        StudyWeeklySubmitApiController.class,

        // StudyReview
        StudyReviewApiController.class,

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

    // AOP Validation
    @MockBean
    private StudyRepository studyRepository;

    @MockBean
    private StudyParticipantRepository studyParticipantRepository;

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

    // Member [Command]
    @MockBean
    protected SignUpMemberUseCase signUpMemberUseCase;

    @MockBean
    protected UpdateMemberUseCase updateMemberUseCase;

    // Member [Query]
    @MockBean
    protected QueryPublicInformationByIdUseCase queryPublicInformationByIdUseCase;

    @MockBean
    protected QueryParticipateStudyByIdUseCase queryParticipateStudyByIdUseCase;

    @MockBean
    protected QueryGraduatedStudyByIdUseCase queryGraduatedStudyByIdUseCase;

    @MockBean
    protected QueryReceivedReviewByIdUseCase queryReceivedReviewByIdUseCase;

    @MockBean
    protected QueryAttendanceRatioByIdUseCase queryAttendanceRatioByIdUseCase;

    @MockBean
    protected QueryPrivateInformationByIdUseCase queryPrivateInformationByIdUseCase;

    @MockBean
    protected QueryAppliedStudyByIdUseCase queryAppliedStudyByIdUseCase;

    @MockBean
    protected QueryLikeMarkedStudyByIdUseCase queryLikeMarkedStudyByIdUseCase;


    // MemberReview
    @MockBean
    protected WriteMemberReviewUseCase writeMemberReviewUseCase;

    @MockBean
    protected UpdateMemberReviewUseCase updateMemberReviewUseCase;

    // MemberReport
    @MockBean
    protected ReportMemberUseCase reportMemberUseCase;

    // Study [Command]
    @MockBean
    protected CreateStudyUseCase createStudyUseCase;

    @MockBean
    protected UpdateStudyUseCase updateStudyUseCase;

    @MockBean
    protected TerminateStudyUseCase terminateStudyUseCase;

    // Study [Query]
    @MockBean
    protected StudyInformationService studyInformationService;

    @MockBean
    protected StudySearchService studySearchService;

    // StudyParticipant
    @MockBean
    protected ApplyStudyUseCase applyStudyUseCase;

    @MockBean
    protected ApplyCancellationUseCase applyCancellationUseCase;

    @MockBean
    protected ApproveParticipationUseCase approveParticipationUseCase;

    @MockBean
    protected RejectParticipationUseCase rejectParticipationUseCase;

    @MockBean
    protected DelegateHostAuthorityUseCase delegateHostAuthorityUseCase;

    @MockBean
    protected LeaveParticipationUseCase leaveParticipationUseCase;

    @MockBean
    protected GraduateStudyUseCase graduateStudyUseCase;

    // StudyAttendance
    @MockBean
    protected ManualAttendanceUseCase manualAttendanceUseCase;

    // StudyNotice & StudyNoticeComment
    @MockBean
    protected WriteStudyNoticeUseCase writeStudyNoticeUseCase;

    @MockBean
    protected UpdateStudyNoticeUseCase updateStudyNoticeUseCase;

    @MockBean
    protected DeleteStudyNoticeUseCase deleteStudyNoticeUseCase;

    @MockBean
    protected WriteStudyNoticeCommentUseCase writeStudyNoticeCommentUseCase;

    @MockBean
    protected UpdateStudyNoticeCommentUseCase updateStudyNoticeCommentUseCase;

    @MockBean
    protected DeleteStudyNoticeCommentUseCase deleteStudyNoticeCommentUseCase;

    // StudyWeekly
    @MockBean
    protected CreateStudyWeeklyUseCase createStudyWeeklyUseCase;

    @MockBean
    protected UpdateStudyWeeklyUseCase updateStudyWeeklyUseCase;

    @MockBean
    protected DeleteStudyWeeklyUseCase deleteStudyWeeklyUseCase;

    @MockBean
    protected SubmitWeeklyAssignmentUseCase submitWeeklyAssignmentUseCase;

    @MockBean
    protected EditSubmittedWeeklyAssignmentUseCase editSubmittedWeeklyAssignmentUseCase;

    // StudyReview
    @MockBean
    protected WriteStudyReviewUseCase writeStudyReviewUseCase;

    @MockBean
    protected UpdateStudyReviewUseCase updateStudyReviewUseCase;

    @MockBean
    protected DeleteStudyReviewUseCase deleteStudyReviewUseCase;

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

    protected void mockingForStudyHost(Long studyId, Long memberId, boolean isValid) {
        given(studyRepository.isHost(studyId, memberId)).willReturn(isValid);
    }

    protected void mockingForStudyParticipant(Long studyId, Long memberId, boolean isValid) {
        given(studyParticipantRepository.isParticipant(studyId, memberId)).willReturn(isValid);
    }
}
