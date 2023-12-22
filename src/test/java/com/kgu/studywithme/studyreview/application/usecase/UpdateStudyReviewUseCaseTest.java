package com.kgu.studywithme.studyreview.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyreview.application.usecase.command.UpdateStudyReviewCommand;
import com.kgu.studywithme.studyreview.domain.model.StudyReview;
import com.kgu.studywithme.studyreview.domain.repository.StudyReviewRepository;
import com.kgu.studywithme.studyreview.exception.StudyReviewErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.ANONYMOUS;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyReview -> UpdateStudyReviewUseCase 테스트")
class UpdateStudyReviewUseCaseTest extends UseCaseTest {
    private final StudyReviewRepository studyReviewRepository = mock(StudyReviewRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final UpdateStudyReviewUseCase sut = new UpdateStudyReviewUseCase(studyReviewRepository, memberRepository);

    private final Member host = JIWON.toMember().apply(1L);
    private final Member member = GHOST.toMember().apply(2L);
    private final Member anonymous = ANONYMOUS.toMember().apply(3L);
    private final Study study = SPRING.toStudy(host).apply(1L);
    private final StudyReview review = StudyReview.writeReview(study, member, "졸업자 리뷰").apply(1L);

    @Test
    @DisplayName("스터디 리뷰 작성자가 아닌 사람이 수정을 시도하면 예외가 발생한다")
    void throwExceptionByMemberIsNotWriter() {
        // given
        final UpdateStudyReviewCommand command = new UpdateStudyReviewCommand(review.getId(), anonymous.getId(), "수정");
        given(studyReviewRepository.getByIdWithWriter(command.reviewId())).willReturn(review);
        given(memberRepository.getById(command.memberId())).willReturn(anonymous);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyReviewErrorCode.ONLY_WRITER_CAN_UPDATE.getMessage());

        assertAll(
                () -> verify(studyReviewRepository, times(1)).getByIdWithWriter(command.reviewId()),
                () -> verify(memberRepository, times(1)).getById(command.memberId())
        );
    }

    @Test
    @DisplayName("작성한 스터디 리뷰를 수정한다")
    void success() {
        // given
        final UpdateStudyReviewCommand command = new UpdateStudyReviewCommand(review.getId(), member.getId(), "수정");
        given(studyReviewRepository.getByIdWithWriter(command.reviewId())).willReturn(review);
        given(memberRepository.getById(command.memberId())).willReturn(member);

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyReviewRepository, times(1)).getByIdWithWriter(command.reviewId()),
                () -> verify(memberRepository, times(1)).getById(command.memberId()),
                () -> assertThat(review.getContent()).isEqualTo(command.content())
        );
    }
}
