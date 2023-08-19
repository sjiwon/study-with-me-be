package com.kgu.studywithme.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgu.studywithme.auth.application.usecase.command.LogoutUseCase;
import com.kgu.studywithme.auth.application.usecase.command.OAuthLoginUseCase;
import com.kgu.studywithme.auth.application.usecase.command.ReissueTokenUseCase;
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
import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.usecase.command.SignUpMemberUseCase;
import com.kgu.studywithme.member.application.usecase.command.UpdateMemberUseCase;
import com.kgu.studywithme.member.application.usecase.query.QueryAppliedStudyByIdUseCase;
import com.kgu.studywithme.member.application.usecase.query.QueryAttendanceRatioByIdUseCase;
import com.kgu.studywithme.member.application.usecase.query.QueryGraduatedStudyByIdUseCase;
import com.kgu.studywithme.member.application.usecase.query.QueryLikeMarkedStudyByIdUseCase;
import com.kgu.studywithme.member.application.usecase.query.QueryParticipateStudyByIdUseCase;
import com.kgu.studywithme.member.application.usecase.query.QueryPrivateInformationByIdUseCase;
import com.kgu.studywithme.member.application.usecase.query.QueryPublicInformationByIdUseCase;
import com.kgu.studywithme.member.application.usecase.query.QueryReceivedReviewByIdUseCase;
import com.kgu.studywithme.member.presentation.MemberApiController;
import com.kgu.studywithme.member.presentation.MemberPrivateInformationApiController;
import com.kgu.studywithme.member.presentation.MemberPublicInformationApiController;
import com.kgu.studywithme.memberreport.application.usecase.command.ReportMemberUseCase;
import com.kgu.studywithme.memberreport.presentation.MemberReportApiController;
import com.kgu.studywithme.memberreview.application.usecase.command.UpdateMemberReviewUseCase;
import com.kgu.studywithme.memberreview.application.usecase.command.WriteMemberReviewUseCase;
import com.kgu.studywithme.memberreview.presentation.MemberReviewApiController;
import com.kgu.studywithme.study.application.adapter.StudyVerificationRepositoryAdapter;
import com.kgu.studywithme.study.application.usecase.command.CreateStudyUseCase;
import com.kgu.studywithme.study.application.usecase.command.TerminateStudyUseCase;
import com.kgu.studywithme.study.application.usecase.command.UpdateStudyUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryApplicantByIdUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryAttendanceByIdUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryBasicInformationByIdUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryNoticeByIdUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryParticipantByIdUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryReviewByIdUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryStudyByCategoryUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryStudyByRecommendUseCase;
import com.kgu.studywithme.study.application.usecase.query.QueryWeeklyByIdUseCase;
import com.kgu.studywithme.study.presentation.StudyApiController;
import com.kgu.studywithme.study.presentation.StudyInformationApiController;
import com.kgu.studywithme.study.presentation.StudyInformationOnlyParticipantApiController;
import com.kgu.studywithme.study.presentation.StudySearchApiController;
import com.kgu.studywithme.studyattendance.application.usecase.command.ManualAttendanceUseCase;
import com.kgu.studywithme.studyattendance.presentation.StudyAttendanceApiController;
import com.kgu.studywithme.studynotice.application.usecase.command.DeleteStudyNoticeCommentUseCase;
import com.kgu.studywithme.studynotice.application.usecase.command.DeleteStudyNoticeUseCase;
import com.kgu.studywithme.studynotice.application.usecase.command.UpdateStudyNoticeCommentUseCase;
import com.kgu.studywithme.studynotice.application.usecase.command.UpdateStudyNoticeUseCase;
import com.kgu.studywithme.studynotice.application.usecase.command.WriteStudyNoticeCommentUseCase;
import com.kgu.studywithme.studynotice.application.usecase.command.WriteStudyNoticeUseCase;
import com.kgu.studywithme.studynotice.presentation.StudyNoticeApiController;
import com.kgu.studywithme.studynotice.presentation.StudyNoticeCommentApiController;
import com.kgu.studywithme.studyparticipant.application.adapter.ParticipantVerificationRepositoryAdapter;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApplyCancellationUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApplyStudyUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApproveParticipationUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.command.DelegateHostAuthorityUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.command.GraduateStudyUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.command.LeaveParticipationUseCase;
import com.kgu.studywithme.studyparticipant.application.usecase.command.RejectParticipationUseCase;
import com.kgu.studywithme.studyparticipant.presentation.DelegateHostAuthorityApiController;
import com.kgu.studywithme.studyparticipant.presentation.StudyApplyApiController;
import com.kgu.studywithme.studyparticipant.presentation.StudyFinalizeApiController;
import com.kgu.studywithme.studyparticipant.presentation.StudyParticipantDecisionApiController;
import com.kgu.studywithme.studyreview.application.usecase.command.DeleteStudyReviewUseCase;
import com.kgu.studywithme.studyreview.application.usecase.command.UpdateStudyReviewUseCase;
import com.kgu.studywithme.studyreview.application.usecase.command.WriteStudyReviewUseCase;
import com.kgu.studywithme.studyreview.presentation.StudyReviewApiController;
import com.kgu.studywithme.studyweekly.application.usecase.command.CreateStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.application.usecase.command.DeleteStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.application.usecase.command.EditWeeklyAssignmentUseCase;
import com.kgu.studywithme.studyweekly.application.usecase.command.SubmitWeeklyAssignmentUseCase;
import com.kgu.studywithme.studyweekly.application.usecase.command.UpdateStudyWeeklyUseCase;
import com.kgu.studywithme.studyweekly.presentation.StudyWeeklyApiController;
import com.kgu.studywithme.studyweekly.presentation.StudyWeeklySubmitApiController;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
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
    private StudyVerificationRepositoryAdapter studyVerificationRepositoryAdapter;

    @MockBean
    private ParticipantVerificationRepositoryAdapter participantVerificationRepositoryAdapter;

    // Auth
    @MockBean
    protected QueryOAuthLinkUseCase queryOAuthLinkUseCase;

    @MockBean
    protected OAuthLoginUseCase oAuthLoginUseCase;

    @MockBean
    protected LogoutUseCase logoutUseCase;

    @MockBean
    protected ReissueTokenUseCase reissueTokenUseCase;

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
    protected QueryBasicInformationByIdUseCase queryBasicInformationByIdUseCase;

    @MockBean
    protected QueryReviewByIdUseCase queryReviewByIdUseCase;

    @MockBean
    protected QueryParticipantByIdUseCase queryParticipantByIdUseCase;

    @MockBean
    protected QueryApplicantByIdUseCase queryApplicantByIdUseCase;

    @MockBean
    protected QueryNoticeByIdUseCase queryNoticeByIdUseCase;

    @MockBean
    protected QueryAttendanceByIdUseCase queryAttendanceByIdUseCase;

    @MockBean
    protected QueryWeeklyByIdUseCase queryWeeklyByIdUseCase;

    @MockBean
    protected QueryStudyByCategoryUseCase queryStudyByCategoryUseCase;

    @MockBean
    protected QueryStudyByRecommendUseCase queryStudyByRecommendUseCase;

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
    protected EditWeeklyAssignmentUseCase editWeeklyAssignmentUseCase;

    // StudyReview
    @MockBean
    protected WriteStudyReviewUseCase writeStudyReviewUseCase;

    @MockBean
    protected UpdateStudyReviewUseCase updateStudyReviewUseCase;

    @MockBean
    protected DeleteStudyReviewUseCase deleteStudyReviewUseCase;

    // Upload
    @MockBean
    protected FileUploader fileUploader;

    @BeforeEach
    void setUp(
            final WebApplicationContext context,
            final RestDocumentationContextProvider provider
    ) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(print())
                .alwaysDo(log())
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    protected String convertObjectToJson(final Object data) throws JsonProcessingException {
        return objectMapper.writeValueAsString(data);
    }

    protected void mockingToken(
            final boolean isValid,
            final Long payloadId
    ) {
        given(jwtTokenProvider.isTokenValid(anyString())).willReturn(isValid);
        given(jwtTokenProvider.getId(anyString())).willReturn(payloadId);
    }

    protected void mockingTokenWithExpiredException() {
        doThrow(StudyWithMeException.type(AuthErrorCode.EXPIRED_TOKEN))
                .when(jwtTokenProvider)
                .isTokenValid(any());
    }

    protected void mockingTokenWithInvalidException() {
        doThrow(StudyWithMeException.type(AuthErrorCode.INVALID_TOKEN))
                .when(jwtTokenProvider)
                .isTokenValid(any());
    }

    protected void mockingForStudyHost(
            final Long studyId,
            final Long memberId,
            final boolean isValid
    ) {
        given(studyVerificationRepositoryAdapter.isHost(studyId, memberId)).willReturn(isValid);
    }

    protected void mockingForStudyParticipant(
            final Long studyId,
            final Long memberId,
            final boolean isValid
    ) {
        given(participantVerificationRepositoryAdapter.isParticipant(studyId, memberId)).willReturn(isValid);
    }
}
