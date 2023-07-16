package com.kgu.studywithme.peerreview.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.peerreview.application.usecase.command.UpdatePeerReviewUseCase;
import com.kgu.studywithme.peerreview.domain.PeerReview;
import com.kgu.studywithme.peerreview.domain.PeerReviewRepository;
import com.kgu.studywithme.peerreview.exception.PeerReviewErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("PeerReview -> UpdatePeerReviewService 테스트")
class UpdatePeerReviewServiceTest extends UseCaseTest {
    @InjectMocks
    private UpdatePeerReviewService updatePeerReviewService;

    @Mock
    private PeerReviewRepository peerReviewRepository;

    private final PeerReview peerReview = PeerReview.doReview(1L, 3L, "Good!!").apply(1L, LocalDateTime.now());

    @Test
    @DisplayName("작성한 피어리뷰가 없다면 수정할 수 없다")
    void throwExceptionByPeerReviewNotFound() {
        // given
        given(peerReviewRepository.findByReviewerIdAndRevieweeId(any(), any()))
                .willReturn(Optional.empty());

        // when - then
        assertThatThrownBy(() -> updatePeerReviewService.updatePeerReview(
                new UpdatePeerReviewUseCase.Command(1L, 2L, "Good!!")
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(PeerReviewErrorCode.PEER_REVIEW_NOT_FOUND.getMessage());

        verify(peerReviewRepository, times(1)).findByReviewerIdAndRevieweeId(any(), any());
    }

    @Test
    @DisplayName("이전과 동일한 내용으로 피어리뷰를 수정할 수 없다")
    void throwExceptionByContentSameAsBefore() {
        // given
        given(peerReviewRepository.findByReviewerIdAndRevieweeId(any(), any()))
                .willReturn(Optional.of(peerReview));

        // when - then
        assertThatThrownBy(() -> updatePeerReviewService.updatePeerReview(
                new UpdatePeerReviewUseCase.Command(1L, 3L, "Good!!")
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(PeerReviewErrorCode.CONTENT_SAME_AS_BEFORE.getMessage());

        verify(peerReviewRepository, times(1)).findByReviewerIdAndRevieweeId(any(), any());
    }

    @Test
    @DisplayName("피어리뷰 수정에 성공한다")
    void success() {
        // given
        given(peerReviewRepository.findByReviewerIdAndRevieweeId(any(), any()))
                .willReturn(Optional.of(peerReview));

        // when
        updatePeerReviewService.updatePeerReview(
                new UpdatePeerReviewUseCase.Command(1L, 3L, "Bad..")
        );

        // then
        verify(peerReviewRepository, times(1)).findByReviewerIdAndRevieweeId(any(), any());
        assertThat(peerReview.getContent()).isEqualTo("Bad..");
    }
}
