package com.kgu.studywithme.memberreview.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.memberreview.application.usecase.command.UpdateMemberReviewUseCase;
import com.kgu.studywithme.memberreview.domain.MemberReview;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
import com.kgu.studywithme.memberreview.infrastructure.persistence.MemberReviewJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("MemberReview -> UpdateMemberReviewService 테스트")
class UpdateMemberReviewServiceTest extends UseCaseTest {
    @InjectMocks
    private UpdateMemberReviewService updateMemberReviewService;

    @Mock
    private MemberReviewJpaRepository memberReviewJpaRepository;

    private final Member memberA = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member memberB = JIWON.toMember().apply(2L, LocalDateTime.now());
    private final MemberReview memberReview = MemberReview.doReview(memberA.getId(), memberB.getId(), "Good!!")
            .apply(1L, LocalDateTime.now());

    @Test
    @DisplayName("해당 사용자에게 작성한 리뷰가 없다면 수정할 수 없다")
    void throwExceptionByMemberReviewNotFound() {
        // given
        given(memberReviewJpaRepository.getWrittenReviewForReviewee(any(), any())).willReturn(Optional.empty());

        // when - then
        assertThatThrownBy(() -> updateMemberReviewService.invoke(
                new UpdateMemberReviewUseCase.Command(memberA.getId(), memberB.getId(), "Bad..")
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.MEMBER_REVIEW_NOT_FOUND.getMessage());

        verify(memberReviewJpaRepository, times(1)).getWrittenReviewForReviewee(any(), any());
    }

    @Test
    @DisplayName("이전과 동일한 내용으로 리뷰를 수정할 수 없다")
    void throwExceptionByContentSameAsBefore() {
        // given
        given(memberReviewJpaRepository.getWrittenReviewForReviewee(any(), any())).willReturn(Optional.of(memberReview));

        // when - then
        assertThatThrownBy(() -> updateMemberReviewService.invoke(
                new UpdateMemberReviewUseCase.Command(memberA.getId(), memberB.getId(), memberReview.getContent())
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.REVIEW_SAME_AS_BEFORE.getMessage());

        verify(memberReviewJpaRepository, times(1)).getWrittenReviewForReviewee(any(), any());
    }

    @Test
    @DisplayName("작성한 리뷰를 수정한다")
    void success() {
        // given
        given(memberReviewJpaRepository.getWrittenReviewForReviewee(any(), any())).willReturn(Optional.of(memberReview));

        // when
        updateMemberReviewService.invoke(new UpdateMemberReviewUseCase.Command(memberA.getId(), memberB.getId(), "Bad.."));

        // then
        assertAll(
                () -> verify(memberReviewJpaRepository, times(1)).getWrittenReviewForReviewee(any(), any()),
                () -> assertThat(memberReview.getContent()).isEqualTo("Bad..")
        );
    }
}
