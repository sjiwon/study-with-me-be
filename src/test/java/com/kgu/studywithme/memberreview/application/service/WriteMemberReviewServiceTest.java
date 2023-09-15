package com.kgu.studywithme.memberreview.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.memberreview.application.usecase.command.WriteMemberReviewUseCase;
import com.kgu.studywithme.memberreview.domain.model.MemberReview;
import com.kgu.studywithme.memberreview.domain.repository.MemberReviewRepository;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
import com.kgu.studywithme.studyattendance.application.adapter.StudyAttendanceHandlingRepositoryAdapter;
import com.kgu.studywithme.studyattendance.infrastructure.query.dto.StudyAttendanceWeekly;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("MemberReview -> WriteMemberReviewService 테스트")
class WriteMemberReviewServiceTest extends UseCaseTest {
    @InjectMocks
    private WriteMemberReviewService writeMemberReviewService;

    @Mock
    private StudyAttendanceHandlingRepositoryAdapter studyAttendanceHandlingRepositoryAdapter;

    @Mock
    private MemberReviewRepository memberReviewRepository;


    private final Member memberA = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member memberB = JIWON.toMember().apply(2L, LocalDateTime.now());
    private final WriteMemberReviewUseCase.Command selfReviewedCommand = new WriteMemberReviewUseCase.Command(
            memberA.getId(),
            memberA.getId(),
            "Good!!"
    );
    private final WriteMemberReviewUseCase.Command command = new WriteMemberReviewUseCase.Command(
            memberA.getId(),
            memberB.getId(),
            "Good!!"
    );

    @Test
    @DisplayName("본인에게 리뷰를 남길 수 없다")
    void throwExceptionBySelfReviewNotAllowed() {
        assertThatThrownBy(() -> writeMemberReviewService.invoke(selfReviewedCommand))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.SELF_REVIEW_NOT_ALLOWED.getMessage());

        assertAll(
                () -> verify(studyAttendanceHandlingRepositoryAdapter, times(0)).findParticipateWeeksInStudyByMemberId(any()),
                () -> verify(memberReviewRepository, times(0)).existsByReviewerIdAndRevieweeId(any(), any()),
                () -> verify(memberReviewRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("함께 스터디를 진행한 기록이 없다면 리뷰를 남길 수 없다")
    void throwExceptionByCommonStudyRecordNotFound() {
        // given
        given(studyAttendanceHandlingRepositoryAdapter.findParticipateWeeksInStudyByMemberId(memberA.getId()))
                .willReturn(List.of(new StudyAttendanceWeekly(1L, 1)));
        given(studyAttendanceHandlingRepositoryAdapter.findParticipateWeeksInStudyByMemberId(memberB.getId()))
                .willReturn(List.of(new StudyAttendanceWeekly(2L, 1)));

        // when - then
        assertThatThrownBy(() -> writeMemberReviewService.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.COMMON_STUDY_RECORD_NOT_FOUND.getMessage());

        assertAll(
                () -> verify(studyAttendanceHandlingRepositoryAdapter, times(2)).findParticipateWeeksInStudyByMemberId(any()),
                () -> verify(memberReviewRepository, times(0)).existsByReviewerIdAndRevieweeId(any(), any()),
                () -> verify(memberReviewRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("해당 사용자에 대해 2번 이상 리뷰를 남길 수 없다")
    void throwExceptionByAlreadyReview() {
        // given
        given(studyAttendanceHandlingRepositoryAdapter.findParticipateWeeksInStudyByMemberId(memberA.getId()))
                .willReturn(List.of(new StudyAttendanceWeekly(1L, 1)));
        given(studyAttendanceHandlingRepositoryAdapter.findParticipateWeeksInStudyByMemberId(memberB.getId()))
                .willReturn(List.of(new StudyAttendanceWeekly(1L, 1)));
        given(memberReviewRepository.existsByReviewerIdAndRevieweeId(any(), any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> writeMemberReviewService.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.ALREADY_REVIEW.getMessage());

        assertAll(
                () -> verify(studyAttendanceHandlingRepositoryAdapter, times(2)).findParticipateWeeksInStudyByMemberId(any()),
                () -> verify(memberReviewRepository, times(1)).existsByReviewerIdAndRevieweeId(any(), any()),
                () -> verify(memberReviewRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("리뷰를 작성한다")
    void success() {
        // given
        given(studyAttendanceHandlingRepositoryAdapter.findParticipateWeeksInStudyByMemberId(memberA.getId()))
                .willReturn(List.of(new StudyAttendanceWeekly(1L, 1)));
        given(studyAttendanceHandlingRepositoryAdapter.findParticipateWeeksInStudyByMemberId(memberB.getId()))
                .willReturn(List.of(new StudyAttendanceWeekly(1L, 1)));
        given(memberReviewRepository.existsByReviewerIdAndRevieweeId(any(), any())).willReturn(false);

        final MemberReview memberReview = MemberReview.doReview(memberA.getId(), memberB.getId(), "Good").apply(1L, LocalDateTime.now());
        given(memberReviewRepository.save(any())).willReturn(memberReview);

        // when
        final Long memberReviewId = writeMemberReviewService.invoke(command);

        // then
        assertAll(
                () -> verify(studyAttendanceHandlingRepositoryAdapter, times(2)).findParticipateWeeksInStudyByMemberId(any()),
                () -> verify(memberReviewRepository, times(1)).existsByReviewerIdAndRevieweeId(any(), any()),
                () -> verify(memberReviewRepository, times(1)).save(any()),
                () -> assertThat(memberReviewId).isEqualTo(memberReview.getId())
        );
    }
}
