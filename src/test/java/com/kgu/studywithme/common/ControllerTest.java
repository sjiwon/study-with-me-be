package com.kgu.studywithme.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgu.studywithme.auth.application.usecase.GetOAuthLinkUseCase;
import com.kgu.studywithme.auth.application.usecase.LogoutUseCase;
import com.kgu.studywithme.auth.application.usecase.OAuthLoginUseCase;
import com.kgu.studywithme.auth.application.usecase.ReissueTokenUseCase;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.presentation.OAuthApiController;
import com.kgu.studywithme.auth.presentation.TokenReissueApiController;
import com.kgu.studywithme.auth.utils.TokenProvider;
import com.kgu.studywithme.category.application.usecase.GetAllCategoriesUseCase;
import com.kgu.studywithme.category.presentation.CategoryApiController;
import com.kgu.studywithme.common.config.TestAopConfiguration;
import com.kgu.studywithme.common.config.TestWebBeanConfiguration;
import com.kgu.studywithme.favorite.application.usecase.CancelStudyLikeUseCase;
import com.kgu.studywithme.favorite.application.usecase.MarkStudyLikeUseCase;
import com.kgu.studywithme.favorite.presentation.FavoriteApiController;
import com.kgu.studywithme.file.application.usecase.UploadImageUseCase;
import com.kgu.studywithme.file.presentation.FileUploadApiController;
import com.kgu.studywithme.global.exception.ErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.global.exception.slack.SlackAlertManager;
import com.kgu.studywithme.member.application.usecase.MemberPrivateQueryUseCase;
import com.kgu.studywithme.member.application.usecase.MemberPublicQueryUseCase;
import com.kgu.studywithme.member.application.usecase.SignUpMemberUseCase;
import com.kgu.studywithme.member.application.usecase.UpdateMemberUseCase;
import com.kgu.studywithme.member.presentation.MemberApiController;
import com.kgu.studywithme.member.presentation.MemberPrivateInformationApiController;
import com.kgu.studywithme.member.presentation.MemberPublicInformationApiController;
import com.kgu.studywithme.memberreport.application.usecase.ReportMemberUseCase;
import com.kgu.studywithme.memberreport.presentation.MemberReportApiController;
import com.kgu.studywithme.memberreview.application.usecase.UpdateMemberReviewUseCase;
import com.kgu.studywithme.memberreview.application.usecase.WriteMemberReviewUseCase;
import com.kgu.studywithme.memberreview.presentation.MemberReviewApiController;
import com.kgu.studywithme.study.application.usecase.CreateStudyUseCase;
import com.kgu.studywithme.study.application.usecase.StudyQueryOnlyParticipantUseCase;
import com.kgu.studywithme.study.application.usecase.StudyQueryUseCase;
import com.kgu.studywithme.study.application.usecase.StudySearchUseCase;
import com.kgu.studywithme.study.application.usecase.TerminateStudyUseCase;
import com.kgu.studywithme.study.application.usecase.UpdateStudyUseCase;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.study.presentation.StudyApiController;
import com.kgu.studywithme.study.presentation.StudyInformationApiController;
import com.kgu.studywithme.study.presentation.StudyInformationOnlyParticipantApiController;
import com.kgu.studywithme.study.presentation.StudySearchApiController;
import com.kgu.studywithme.studyattendance.application.usecase.ManualAttendanceUseCase;
import com.kgu.studywithme.studyattendance.presentation.StudyAttendanceApiController;
import com.kgu.studywithme.studynotice.application.usecase.DeleteStudyNoticeCommentUseCase;
import com.kgu.studywithme.studynotice.application.usecase.DeleteStudyNoticeUseCase;
import com.kgu.studywithme.studynotice.application.usecase.UpdateStudyNoticeCommentUseCase;
import com.kgu.studywithme.studynotice.application.usecase.UpdateStudyNoticeUseCase;
import com.kgu.studywithme.studynotice.application.usecase.WriteStudyNoticeCommentUseCase;
import com.kgu.studywithme.studynotice.application.usecase.WriteStudyNoticeUseCase;
import com.kgu.studywithme.studynotice.presentation.StudyNoticeApiController;
import com.kgu.studywithme.studynotice.presentation.StudyNoticeCommentApiController;
import com.kgu.studywithme.studyparticipant.application.usecase.ApplyCancelUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.ApplyStudyUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.ApproveParticipationUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.DelegateHostAuthorityUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.GraduateStudyUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.LeaveStudyUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.RejectParticipationUseCase;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.presentation.DelegateHostAuthorityApiController;
import com.kgu.studywithme.studyparticipant.presentation.StudyApplyApiController;
import com.kgu.studywithme.studyparticipant.presentation.StudyFinalizeApiController;
import com.kgu.studywithme.studyparticipant.presentation.StudyParticipantDecisionApiController;
import com.kgu.studywithme.studyreview.application.usecase.command.DeleteStudyReviewUseCase;
import com.kgu.studywithme.studyreview.application.usecase.command.UpdateStudyReviewUseCase;
import com.kgu.studywithme.studyreview.application.usecase.command.WriteStudyReviewUseCase;
import com.kgu.studywithme.studyreview.presentation.StudyReviewApiController;
import com.kgu.studywithme.studyweekly.application.service.AssignmentUploader;
import com.kgu.studywithme.studyweekly.application.service.AttachmentUploader;
import com.kgu.studywithme.studyweekly.application.usecase.command.CreateStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.application.usecase.command.DeleteStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.application.usecase.command.EditWeeklyAssignmentUseCase;
import com.kgu.studywithme.studyweekly.application.usecase.command.SubmitWeeklyAssignmentUseCase;
import com.kgu.studywithme.studyweekly.application.usecase.command.UpdateStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.presentation.StudyWeeklyApiController;
import com.kgu.studywithme.studyweekly.presentation.StudyWeeklySubmitApiController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Tag("Controller")
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
        StudyInformationOnlyParticipantApiController.class,
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

        // File
        FileUploadApiController.class,
})
@ExtendWith(RestDocumentationExtension.class)
@Import({TestAopConfiguration.class, TestWebBeanConfiguration.class})
@AutoConfigureRestDocs
public abstract class ControllerTest {
    // common & external
    @Autowired
    protected MockMvc mockMvc;

    // common & internal
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private SlackAlertManager slackAlertManager;

    // AOP Validation
    @MockBean
    private StudyRepository studyRepository;

    @MockBean
    private StudyParticipantRepository studyParticipantRepository;

    // Auth
    @MockBean
    protected GetOAuthLinkUseCase getOAuthLinkUseCase;

    @MockBean
    protected OAuthLoginUseCase oAuthLoginUseCase;

    @MockBean
    protected LogoutUseCase logoutUseCase;

    @MockBean
    protected ReissueTokenUseCase reissueTokenUseCase;

    // Category & Favorite
    @MockBean
    protected GetAllCategoriesUseCase getAllCategoriesUseCase;

    @MockBean
    protected MarkStudyLikeUseCase markStudyLikeUseCase;

    @MockBean
    protected CancelStudyLikeUseCase cancelStudyLikeUseCase;

    // Member [Command]
    @MockBean
    protected SignUpMemberUseCase signUpMemberUseCase;

    @MockBean
    protected UpdateMemberUseCase updateMemberUseCase;

    // Member [Query]
    @MockBean
    protected MemberPublicQueryUseCase memberPublicQueryUseCase;

    @MockBean
    protected MemberPrivateQueryUseCase memberPrivateQueryUseCase;

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
    protected StudyQueryUseCase studyQueryUseCase;

    @MockBean
    protected StudyQueryOnlyParticipantUseCase studyQueryOnlyParticipantUseCase;

    @MockBean
    protected StudySearchUseCase studySearchUseCase;

    // StudyParticipant
    @MockBean
    protected ApplyStudyUseCase applyStudyUseCase;

    @MockBean
    protected ApplyCancelUseCase applyCancelUseCase;

    @MockBean
    protected ApproveParticipationUseCase approveParticipationUseCase;

    @MockBean
    protected RejectParticipationUseCase rejectParticipationUseCase;

    @MockBean
    protected DelegateHostAuthorityUseCase delegateHostAuthorityUseCase;

    @MockBean
    protected LeaveStudyUseCase leaveStudyUseCase;

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
    protected AttachmentUploader attachmentUploader;

    @MockBean
    protected CreateStudyWeeklyUseCase createStudyWeeklyUseCase;

    @MockBean
    protected UpdateStudyWeeklyUseCase updateStudyWeeklyUseCase;

    @MockBean
    protected DeleteStudyWeeklyUseCase deleteStudyWeeklyUseCase;

    @MockBean
    protected AssignmentUploader assignmentUploader;

    @MockBean
    protected SubmitWeeklyAssignmentUseCase submitWeeklyAssignmentUseCase;

    @MockBean
    protected EditWeeklyAssignmentUseCase editWeeklyAssignmentUseCase;

    // StudyReview
    @MockBean
    protected WriteStudyReviewUseCase writeStudyReviewUseCase;

    @MockBean
    protected UpdateStudyReviewUseCase updateStudyReviewUseCase;

    @MockBean
    protected DeleteStudyReviewUseCase deleteStudyReviewUseCase;

    // File
    @MockBean
    protected UploadImageUseCase uploadImageUseCase;

    @BeforeEach
    void setUp(final WebApplicationContext context, final RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(print())
                .alwaysDo(log())
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    protected ResultMatcher[] getResultMatchersViaErrorCode(final ErrorCode eror) {
        return new ResultMatcher[]{
                jsonPath("$.status").exists(),
                jsonPath("$.status").value(eror.getStatus().value()),
                jsonPath("$.errorCode").exists(),
                jsonPath("$.errorCode").value(eror.getErrorCode()),
                jsonPath("$.message").exists(),
                jsonPath("$.message").value(eror.getMessage())
        };
    }

    protected ResultMatcher[] getResultMatchersViaErrorCode(final ErrorCode eror, final String message) {
        return new ResultMatcher[]{
                jsonPath("$.status").exists(),
                jsonPath("$.status").value(eror.getStatus().value()),
                jsonPath("$.errorCode").exists(),
                jsonPath("$.errorCode").value(eror.getErrorCode()),
                jsonPath("$.message").exists(),
                jsonPath("$.message").value(message)
        };
    }

    protected String convertObjectToJson(final Object data) throws JsonProcessingException {
        return objectMapper.writeValueAsString(data);
    }

    protected void mockingToken(
            final boolean isValid,
            final Long payloadId
    ) {
        given(tokenProvider.isTokenValid(anyString())).willReturn(isValid);
        given(tokenProvider.getId(anyString())).willReturn(payloadId);
    }

    protected void mockingTokenWithExpiredException() {
        doThrow(StudyWithMeException.type(AuthErrorCode.EXPIRED_TOKEN))
                .when(tokenProvider)
                .isTokenValid(any());
    }

    protected void mockingTokenWithInvalidException() {
        doThrow(StudyWithMeException.type(AuthErrorCode.INVALID_TOKEN))
                .when(tokenProvider)
                .isTokenValid(any());
    }

    protected void mockingForStudyHost(final Long studyId, final Long memberId, final boolean isValue) {
        given(studyRepository.isHost(studyId, memberId)).willReturn(isValue);
    }

    protected void mockingForStudyParticipant(final Long studyId, final Long memberId, final boolean isValid) {
        given(studyParticipantRepository.isParticipant(studyId, memberId)).willReturn(isValid);
    }
}
