package com.kgu.studywithme.studyreview.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyreview.application.usecase.command.DeleteStudyReviewUseCase;
import com.kgu.studywithme.studyreview.domain.StudyReview;
import com.kgu.studywithme.studyreview.exception.StudyReviewErrorCode;
import com.kgu.studywithme.studyreview.infrastructure.persistence.StudyReviewJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyReview -> DeleteStudyReviewService 테스트")
class DeleteStudyReviewServiceTest extends UseCaseTest {
    @InjectMocks
    private DeleteStudyReviewService deleteStudyReviewService;

    @Mock
    private StudyReviewJpaRepository studyReviewJpaRepository;

    private final Member member = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member anonymous = GHOST.toMember().apply(2L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(member.getId()).apply(1L, LocalDateTime.now());
    private final StudyReview review = StudyReview.writeReview(
            study.getId(),
            member.getId(),
            "졸업자 리뷰"
    ).apply(1L, LocalDateTime.now());

    @Test
    @DisplayName("스터디 리뷰 작성자가 아닌 사람이 삭제를 시도하면 예외가 발생한다")
    void throwExceptionByMemberIsNotWriter() {
        // given
        given(studyReviewJpaRepository.findById(any())).willReturn(Optional.of(review));

        // when - then
        assertThatThrownBy(() -> deleteStudyReviewService.invoke(
                new DeleteStudyReviewUseCase.Command(review.getId(), anonymous.getId())
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyReviewErrorCode.ONLY_WRITER_CAN_DELETE.getMessage());

        assertAll(
                () -> verify(studyReviewJpaRepository, times(1)).findById(any()),
                () -> verify(studyReviewJpaRepository, times(0)).delete(any())
        );
    }

    @Test
    @DisplayName("작성한 스터디 리뷰를 삭제한다")
    void success() {
        // given
        given(studyReviewJpaRepository.findById(any())).willReturn(Optional.of(review));

        // when
        deleteStudyReviewService.invoke(new DeleteStudyReviewUseCase.Command(review.getId(), member.getId()));

        // then
        assertAll(
                () -> verify(studyReviewJpaRepository, times(1)).findById(any()),
                () -> verify(studyReviewJpaRepository, times(1)).delete(any())
        );
    }
}
