package com.kgu.studywithme.memberreview.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.memberreview.application.usecase.command.UpdateMemberReviewUseCase;
import com.kgu.studywithme.memberreview.domain.MemberReview;
import com.kgu.studywithme.memberreview.domain.MemberReviewRepository;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
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

@DisplayName("MemberReview -> UpdateMemberReviewService 테스트")
class UpdateMemberReviewServiceTest extends UseCaseTest {
    @InjectMocks
    private UpdateMemberReviewService updateMemberReviewService;

    @Mock
    private MemberReviewRepository memberReviewRepository;

    private final MemberReview memberReview = MemberReview.doReview(1L, 3L, "Good!!").apply(1L, LocalDateTime.now());

    @Test
    @DisplayName("해당 사용자에게 작성한 리뷰가 없다면 수정할 수 없다")
    void throwExceptionByMemberReviewNotFound() {
        // given
        given(memberReviewRepository.findByReviewerIdAndRevieweeId(any(), any()))
                .willReturn(Optional.empty());

        // when - then
        assertThatThrownBy(() -> updateMemberReviewService.updateMemberReview(
                new UpdateMemberReviewUseCase.Command(1L, 2L, "Good!!")
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.MEMBER_REVIEW_NOT_FOUND.getMessage());

        verify(memberReviewRepository, times(1)).findByReviewerIdAndRevieweeId(any(), any());
    }

    @Test
    @DisplayName("이전과 동일한 내용으로 리뷰를 수정할 수 없다")
    void throwExceptionByContentSameAsBefore() {
        // given
        given(memberReviewRepository.findByReviewerIdAndRevieweeId(any(), any()))
                .willReturn(Optional.of(memberReview));

        // when - then
        assertThatThrownBy(() -> updateMemberReviewService.updateMemberReview(
                new UpdateMemberReviewUseCase.Command(1L, 3L, "Good!!")
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.CONTENT_SAME_AS_BEFORE.getMessage());

        verify(memberReviewRepository, times(1)).findByReviewerIdAndRevieweeId(any(), any());
    }

    @Test
    @DisplayName("리뷰 수정에 성공한다")
    void success() {
        // given
        given(memberReviewRepository.findByReviewerIdAndRevieweeId(any(), any()))
                .willReturn(Optional.of(memberReview));

        // when
        updateMemberReviewService.updateMemberReview(
                new UpdateMemberReviewUseCase.Command(1L, 3L, "Bad..")
        );

        // then
        verify(memberReviewRepository, times(1)).findByReviewerIdAndRevieweeId(any(), any());
        assertThat(memberReview.getContent()).isEqualTo("Bad..");
    }
}
