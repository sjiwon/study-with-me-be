package com.kgu.studywithme.peerreview.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.StudyParticipateWeeks;
import com.kgu.studywithme.peerreview.application.usecase.command.WritePeerReviewUseCase;
import com.kgu.studywithme.peerreview.domain.PeerReview;
import com.kgu.studywithme.peerreview.domain.PeerReviewRepository;
import com.kgu.studywithme.peerreview.exception.PeerReviewErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

@DisplayName("PeerReview -> WritePeerReviewService 테스트")
class WritePeerReviewServiceTest extends UseCaseTest {
    @InjectMocks
    private WritePeerReviewService writePeerReviewService;

    @Mock
    private PeerReviewRepository peerReviewRepository;

    @Mock
    private MemberRepository memberRepository;

    private final WritePeerReviewUseCase.Command selfReviewedCommand =
            new WritePeerReviewUseCase.Command(1L, 1L, "Good!!");

    private final WritePeerReviewUseCase.Command command =
            new WritePeerReviewUseCase.Command(1L, 2L, "Good!!");

    @Nested
    @DisplayName("피어리뷰 작성")
    class writeReview {
        @Test
        @DisplayName("본인에게 피어리뷰를 남길 수 없다")
        void throwExceptionBySelfReviewNotAllowed() {
            assertThatThrownBy(() -> writePeerReviewService.writePeerReview(selfReviewedCommand))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(PeerReviewErrorCode.SELF_REVIEW_NOT_ALLOWED.getMessage());

            verify(memberRepository, times(0)).findParticipateWeeksInStudyByMemberId(any());
            verify(peerReviewRepository, times(0)).existsByReviewerIdAndRevieweeId(any(), any());
            verify(peerReviewRepository, times(0)).save(any());
        }

        @Test
        @DisplayName("함께 스터디를 진행한 기록이 없다면 피어리뷰를 남길 수 없다")
        void throwExceptionByCommonStudyRecordNotFound() {
            // given
            given(memberRepository.findParticipateWeeksInStudyByMemberId(1L))
                    .willReturn(List.of(new StudyParticipateWeeks(1L, 1)));
            given(memberRepository.findParticipateWeeksInStudyByMemberId(2L))
                    .willReturn(List.of(new StudyParticipateWeeks(2L, 1)));

            // when - then
            assertThatThrownBy(() -> writePeerReviewService.writePeerReview(command))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(PeerReviewErrorCode.COMMON_STUDY_RECORD_NOT_FOUND.getMessage());

            verify(memberRepository, times(2)).findParticipateWeeksInStudyByMemberId(any());
            verify(peerReviewRepository, times(0)).existsByReviewerIdAndRevieweeId(any(), any());
            verify(peerReviewRepository, times(0)).save(any());
        }

        @Test
        @DisplayName("해당 사용자에 대해 두 번이상 피어리뷰를 남길 수 없다")
        void throwExceptionByAlreadyReview() {
            // given
            given(memberRepository.findParticipateWeeksInStudyByMemberId(1L))
                    .willReturn(List.of(new StudyParticipateWeeks(1L, 1)));
            given(memberRepository.findParticipateWeeksInStudyByMemberId(2L))
                    .willReturn(List.of(new StudyParticipateWeeks(1L, 1)));
            given(peerReviewRepository.existsByReviewerIdAndRevieweeId(1L, 2L))
                    .willReturn(true);

            // when - then
            assertThatThrownBy(() -> writePeerReviewService.writePeerReview(command))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(PeerReviewErrorCode.ALREADY_REVIEW.getMessage());

            verify(memberRepository, times(2)).findParticipateWeeksInStudyByMemberId(any());
            verify(peerReviewRepository, times(1)).existsByReviewerIdAndRevieweeId(any(), any());
            verify(peerReviewRepository, times(0)).save(any());
        }

        @Test
        @DisplayName("피어리뷰 작성에 성공한다")
        void success() {
            // given
            given(memberRepository.findParticipateWeeksInStudyByMemberId(1L))
                    .willReturn(List.of(new StudyParticipateWeeks(1L, 1)));
            given(memberRepository.findParticipateWeeksInStudyByMemberId(2L))
                    .willReturn(List.of(new StudyParticipateWeeks(1L, 1)));
            given(peerReviewRepository.existsByReviewerIdAndRevieweeId(1L, 2L))
                    .willReturn(false);

            final PeerReview peerReview = PeerReview.doReview(1L, 2L, "Good").apply(1L, LocalDateTime.now());
            given(peerReviewRepository.save(any())).willReturn(peerReview);

            // when
            Long peerReviewId = writePeerReviewService.writePeerReview(command);

            // then
            verify(memberRepository, times(2)).findParticipateWeeksInStudyByMemberId(any());
            verify(peerReviewRepository, times(1)).existsByReviewerIdAndRevieweeId(any(), any());
            verify(peerReviewRepository, times(1)).save(any());
            assertThat(peerReviewId).isEqualTo(peerReview.getId());
        }
    }
}
