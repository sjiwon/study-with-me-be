package com.kgu.studywithme.memberreview.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.StudyParticipateWeeks;
import com.kgu.studywithme.memberreview.application.usecase.command.WriteMemberReviewUseCase;
import com.kgu.studywithme.memberreview.domain.MemberReview;
import com.kgu.studywithme.memberreview.domain.MemberReviewRepository;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("MemberReview -> WriteMemberReviewService 테스트")
class WriteMemberReviewServiceTest extends UseCaseTest {
    @InjectMocks
    private WriteMemberReviewService writeMemberReviewService;

    @Mock
    private MemberReviewRepository memberReviewRepository;

    @Mock
    private MemberRepository memberRepository;

    private final WriteMemberReviewUseCase.Command selfReviewedCommand =
            new WriteMemberReviewUseCase.Command(1L, 1L, "Good!!");

    private final WriteMemberReviewUseCase.Command command =
            new WriteMemberReviewUseCase.Command(1L, 2L, "Good!!");

    @Test
    @DisplayName("본인에게 리뷰를 남길 수 없다")
    void throwExceptionBySelfReviewNotAllowed() {
        assertThatThrownBy(() -> writeMemberReviewService.writeMemberReview(selfReviewedCommand))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.SELF_REVIEW_NOT_ALLOWED.getMessage());

        verify(memberRepository, times(0)).findParticipateWeeksInStudyByMemberId(any());
        verify(memberReviewRepository, times(0)).existsByReviewerIdAndRevieweeId(any(), any());
        verify(memberReviewRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("함께 스터디를 진행한 기록이 없다면 리뷰를 남길 수 없다")
    void throwExceptionByCommonStudyRecordNotFound() {
        // given
        given(memberRepository.findParticipateWeeksInStudyByMemberId(1L))
                .willReturn(List.of(new StudyParticipateWeeks(1L, 1)));
        given(memberRepository.findParticipateWeeksInStudyByMemberId(2L))
                .willReturn(List.of(new StudyParticipateWeeks(2L, 1)));

        // when - then
        assertThatThrownBy(() -> writeMemberReviewService.writeMemberReview(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.COMMON_STUDY_RECORD_NOT_FOUND.getMessage());

        verify(memberRepository, times(2)).findParticipateWeeksInStudyByMemberId(any());
        verify(memberReviewRepository, times(0)).existsByReviewerIdAndRevieweeId(any(), any());
        verify(memberReviewRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("해당 사용자에 대해 2번 이상 리뷰를 남길 수 없다")
    void throwExceptionByAlreadyReview() {
        // given
        given(memberRepository.findParticipateWeeksInStudyByMemberId(1L))
                .willReturn(List.of(new StudyParticipateWeeks(1L, 1)));
        given(memberRepository.findParticipateWeeksInStudyByMemberId(2L))
                .willReturn(List.of(new StudyParticipateWeeks(1L, 1)));
        given(memberReviewRepository.existsByReviewerIdAndRevieweeId(1L, 2L))
                .willReturn(true);

        // when - then
        assertThatThrownBy(() -> writeMemberReviewService.writeMemberReview(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.ALREADY_REVIEW.getMessage());

        verify(memberRepository, times(2)).findParticipateWeeksInStudyByMemberId(any());
        verify(memberReviewRepository, times(1)).existsByReviewerIdAndRevieweeId(any(), any());
        verify(memberReviewRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("리뷰 작성에 성공한다")
    void success() {
        // given
        given(memberRepository.findParticipateWeeksInStudyByMemberId(1L))
                .willReturn(List.of(new StudyParticipateWeeks(1L, 1)));
        given(memberRepository.findParticipateWeeksInStudyByMemberId(2L))
                .willReturn(List.of(new StudyParticipateWeeks(1L, 1)));
        given(memberReviewRepository.existsByReviewerIdAndRevieweeId(1L, 2L))
                .willReturn(false);

        final MemberReview memberReview = MemberReview.doReview(1L, 2L, "Good").apply(1L, LocalDateTime.now());
        given(memberReviewRepository.save(any())).willReturn(memberReview);

        // when
        Long memberReviewId = writeMemberReviewService.writeMemberReview(command);

        // then
        verify(memberRepository, times(2)).findParticipateWeeksInStudyByMemberId(any());
        verify(memberReviewRepository, times(1)).existsByReviewerIdAndRevieweeId(any(), any());
        verify(memberReviewRepository, times(1)).save(any());
        assertThat(memberReviewId).isEqualTo(memberReview.getId());
    }
}
