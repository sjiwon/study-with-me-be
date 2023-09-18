package com.kgu.studywithme.studyreview.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyreview.application.usecase.command.DeleteStudyReviewCommand;
import com.kgu.studywithme.studyreview.domain.model.StudyReview;
import com.kgu.studywithme.studyreview.domain.repository.StudyReviewRepository;
import com.kgu.studywithme.studyreview.exception.StudyReviewErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.ANONYMOUS;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyReview -> DeleteStudyReviewUseCase 테스트")
class DeleteStudyReviewUseCaseTest extends UseCaseTest {
    private final StudyReviewRepository studyReviewRepository = mock(StudyReviewRepository.class);
    private final DeleteStudyReviewUseCase sut = new DeleteStudyReviewUseCase(studyReviewRepository);

    private final Member host = JIWON.toMember().apply(1L);
    private final Member member = GHOST.toMember().apply(2L);
    private final Member anonymous = ANONYMOUS.toMember().apply(3L);
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L);
    private final StudyReview review = StudyReview.writeReview(study.getId(), member.getId(), "졸업자 리뷰").apply(1L);
    private final DeleteStudyReviewCommand anonymousCommand = new DeleteStudyReviewCommand(review.getId(), anonymous.getId());
    private final DeleteStudyReviewCommand command = new DeleteStudyReviewCommand(review.getId(), review.getWriterId());

    @Test
    @DisplayName("스터디 리뷰 작성자가 아닌 사람이 삭제를 시도하면 예외가 발생한다")
    void throwExceptionByMemberIsNotWriter() {
        // given
        given(studyReviewRepository.getById(anonymousCommand.reviewId())).willReturn(review);

        // when - then
        assertThatThrownBy(() -> sut.invoke(anonymousCommand))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyReviewErrorCode.ONLY_WRITER_CAN_DELETE.getMessage());

        assertAll(
                () -> verify(studyReviewRepository, times(1)).getById(anonymousCommand.reviewId()),
                () -> verify(studyReviewRepository, times(0)).delete(review)
        );
    }

    @Test
    @DisplayName("작성한 스터디 리뷰를 삭제한다")
    void success() {
        // given
        given(studyReviewRepository.getById(command.reviewId())).willReturn(review);

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyReviewRepository, times(1)).getById(command.reviewId()),
                () -> verify(studyReviewRepository, times(1)).delete(review)
        );
    }
}
