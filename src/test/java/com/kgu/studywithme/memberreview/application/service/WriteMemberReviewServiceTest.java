package com.kgu.studywithme.memberreview.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.adapter.MemberAttendanceRepositoryAdapter;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.infrastructure.query.dto.StudyParticipateWeeks;
import com.kgu.studywithme.memberreview.application.adapter.MemberReviewHandlingRepositoryAdapter;
import com.kgu.studywithme.memberreview.application.usecase.command.WriteMemberReviewUseCase;
import com.kgu.studywithme.memberreview.domain.MemberReview;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
import com.kgu.studywithme.memberreview.infrastructure.persistence.MemberReviewJpaRepository;
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
    private MemberAttendanceRepositoryAdapter memberAttendanceRepositoryAdapter;

    @Mock
    private MemberReviewHandlingRepositoryAdapter memberReviewHandlingRepositoryAdapter;

    @Mock
    private MemberReviewJpaRepository memberReviewJpaRepository;


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
                () -> verify(memberAttendanceRepositoryAdapter, times(0)).findParticipateWeeksInStudyByMemberId(any()),
                () -> verify(memberReviewHandlingRepositoryAdapter, times(0)).alreadyReviewedForMember(any(), any()),
                () -> verify(memberReviewJpaRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("함께 스터디를 진행한 기록이 없다면 리뷰를 남길 수 없다")
    void throwExceptionByCommonStudyRecordNotFound() {
        // given
        given(memberAttendanceRepositoryAdapter.findParticipateWeeksInStudyByMemberId(memberA.getId()))
                .willReturn(List.of(new StudyParticipateWeeks(1L, 1)));
        given(memberAttendanceRepositoryAdapter.findParticipateWeeksInStudyByMemberId(memberB.getId()))
                .willReturn(List.of(new StudyParticipateWeeks(2L, 1)));

        // when - then
        assertThatThrownBy(() -> writeMemberReviewService.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.COMMON_STUDY_RECORD_NOT_FOUND.getMessage());

        assertAll(
                () -> verify(memberAttendanceRepositoryAdapter, times(2)).findParticipateWeeksInStudyByMemberId(any()),
                () -> verify(memberReviewHandlingRepositoryAdapter, times(0)).alreadyReviewedForMember(any(), any()),
                () -> verify(memberReviewJpaRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("해당 사용자에 대해 2번 이상 리뷰를 남길 수 없다")
    void throwExceptionByAlreadyReview() {
        // given
        given(memberAttendanceRepositoryAdapter.findParticipateWeeksInStudyByMemberId(memberA.getId()))
                .willReturn(List.of(new StudyParticipateWeeks(1L, 1)));
        given(memberAttendanceRepositoryAdapter.findParticipateWeeksInStudyByMemberId(memberB.getId()))
                .willReturn(List.of(new StudyParticipateWeeks(1L, 1)));
        given(memberReviewHandlingRepositoryAdapter.alreadyReviewedForMember(any(), any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> writeMemberReviewService.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.ALREADY_REVIEW.getMessage());

        assertAll(
                () -> verify(memberAttendanceRepositoryAdapter, times(2)).findParticipateWeeksInStudyByMemberId(any()),
                () -> verify(memberReviewHandlingRepositoryAdapter, times(1)).alreadyReviewedForMember(any(), any()),
                () -> verify(memberReviewJpaRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("리뷰를 작성한다")
    void success() {
        // given
        given(memberAttendanceRepositoryAdapter.findParticipateWeeksInStudyByMemberId(memberA.getId()))
                .willReturn(List.of(new StudyParticipateWeeks(1L, 1)));
        given(memberAttendanceRepositoryAdapter.findParticipateWeeksInStudyByMemberId(memberB.getId()))
                .willReturn(List.of(new StudyParticipateWeeks(1L, 1)));
        given(memberReviewHandlingRepositoryAdapter.alreadyReviewedForMember(any(), any())).willReturn(false);

        final MemberReview memberReview = MemberReview.doReview(memberA.getId(), memberB.getId(), "Good").apply(1L, LocalDateTime.now());
        given(memberReviewJpaRepository.save(any())).willReturn(memberReview);

        // when
        final Long memberReviewId = writeMemberReviewService.invoke(command);

        // then
        assertAll(
                () -> verify(memberAttendanceRepositoryAdapter, times(2)).findParticipateWeeksInStudyByMemberId(any()),
                () -> verify(memberReviewHandlingRepositoryAdapter, times(1)).alreadyReviewedForMember(any(), any()),
                () -> verify(memberReviewJpaRepository, times(1)).save(any()),
                () -> assertThat(memberReviewId).isEqualTo(memberReview.getId())
        );
    }
}
