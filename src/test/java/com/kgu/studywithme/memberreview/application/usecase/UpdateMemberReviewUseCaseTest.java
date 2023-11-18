package com.kgu.studywithme.memberreview.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.memberreview.application.usecase.command.UpdateMemberReviewCommand;
import com.kgu.studywithme.memberreview.domain.model.MemberReview;
import com.kgu.studywithme.memberreview.domain.repository.MemberReviewRepository;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("MemberReview -> UpdateMemberReviewUseCase 테스트")
class UpdateMemberReviewUseCaseTest extends UseCaseTest {
    private final MemberReviewRepository memberReviewRepository = mock(MemberReviewRepository.class);
    private final UpdateMemberReviewUseCase sut = new UpdateMemberReviewUseCase(memberReviewRepository);

    private final Member memberA = JIWON.toMember().apply(1L);
    private final Member memberB = JIWON.toMember().apply(2L);
    private final MemberReview review = MemberReview.doReview(memberA, memberB, "Good!!").apply(1L);

    @Test
    @DisplayName("해당 사용자에게 작성한 리뷰가 없다면 수정할 수 없다")
    void throwExceptionByMemberReviewNotFound() {
        // given
        doThrow(StudyWithMeException.type(MemberReviewErrorCode.MEMBER_REVIEW_NOT_FOUND))
                .when(memberReviewRepository)
                .getWrittenReview(memberA.getId(), memberB.getId());

        // when - then
        assertThatThrownBy(() -> sut.invoke(new UpdateMemberReviewCommand(memberA.getId(), memberB.getId(), "Bad..")))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.MEMBER_REVIEW_NOT_FOUND.getMessage());

        verify(memberReviewRepository, times(1)).getWrittenReview(memberA.getId(), memberB.getId());
    }

    @Test
    @DisplayName("이전과 동일한 내용으로 리뷰를 수정할 수 없다")
    void throwExceptionByContentSameAsBefore() {
        // given
        given(memberReviewRepository.getWrittenReview(memberA.getId(), memberB.getId())).willReturn(review);

        // when - then
        assertThatThrownBy(() -> sut.invoke(new UpdateMemberReviewCommand(memberA.getId(), memberB.getId(), review.getContent())))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReviewErrorCode.REVIEW_SAME_AS_BEFORE.getMessage());

        verify(memberReviewRepository, times(1)).getWrittenReview(memberA.getId(), memberB.getId());
    }

    @Test
    @DisplayName("작성한 리뷰를 수정한다")
    void success() {
        // given
        given(memberReviewRepository.getWrittenReview(memberA.getId(), memberB.getId())).willReturn(review);

        // when
        sut.invoke(new UpdateMemberReviewCommand(memberA.getId(), memberB.getId(), "Bad.."));

        // then
        assertAll(
                () -> verify(memberReviewRepository, times(1)).getWrittenReview(memberA.getId(), memberB.getId()),
                () -> assertThat(review.getContent()).isEqualTo("Bad..")
        );
    }
}
