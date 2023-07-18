package com.kgu.studywithme.studyreview.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyreview.application.usecase.command.UpdateStudyReviewUseCase;
import com.kgu.studywithme.studyreview.domain.StudyReview;
import com.kgu.studywithme.studyreview.domain.StudyReviewRepository;
import com.kgu.studywithme.studyreview.exception.StudyReviewErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyReview -> UpdateStudyReviewService 테스트")
class UpdateStudyReviewServiceTest extends UseCaseTest {
    @InjectMocks
    private UpdateStudyReviewService updateStudyReviewService;

    @Mock
    private StudyReviewRepository studyReviewRepository;

    private final Member member = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member anonymous = GHOST.toMember().apply(2L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(member).apply(1L, LocalDateTime.now());
    private final StudyReview review = StudyReview.writeReview(
            study.getId(),
            member.getId(),
            "졸업자 리뷰"
    ).apply(1L, LocalDateTime.now());

    @Test
    @DisplayName("스터디 리뷰 작성자가 아닌 사람이 수정을 시도하면 예외가 발생한다")
    void throwExceptionByMemberIsNotWriter() {
        // given
        given(studyReviewRepository.findById(any())).willReturn(Optional.of(review));

        // when - then
        assertThatThrownBy(() -> updateStudyReviewService.updateStudyReview(
                new UpdateStudyReviewUseCase.Command(
                        review.getId(),
                        anonymous.getId(),
                        "졸업자 리뷰 - 수정"
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyReviewErrorCode.ONLY_WRITER_CAN_UPDATE.getMessage());
        verify(studyReviewRepository, times(1)).findById(any());
    }

    @Test
    @DisplayName("작성한 스터디 리뷰를 수정한다")
    void success() {
        // given
        given(studyReviewRepository.findById(any())).willReturn(Optional.of(review));

        // when
        updateStudyReviewService.updateStudyReview(
                new UpdateStudyReviewUseCase.Command(
                        review.getId(),
                        member.getId(),
                        "졸업자 리뷰 - 수정"
                )
        );

        // then
        verify(studyReviewRepository, times(1)).findById(any());
        assertThat(review.getContent()).isEqualTo("졸업자 리뷰 - 수정");
    }
}
