package com.kgu.studywithme.studyreview.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import com.kgu.studywithme.studyreview.application.usecase.command.WriteStudyReviewUseCase;
import com.kgu.studywithme.studyreview.domain.StudyReview;
import com.kgu.studywithme.studyreview.domain.StudyReviewRepository;
import com.kgu.studywithme.studyreview.exception.StudyReviewErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyReview -> WriteStudyReviewService 테스트")
class WriteStudyReviewServiceTest extends UseCaseTest {
    @InjectMocks
    private WriteStudyReviewService writeStudyReviewService;

    @Mock
    private StudyParticipantRepository studyParticipantRepository;

    @Mock
    private StudyReviewRepository studyReviewRepository;

    private final Member member = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(member.getId()).apply(1L, LocalDateTime.now());
    private final WriteStudyReviewUseCase.Command command =
            new WriteStudyReviewUseCase.Command(
                    study.getId(),
                    member.getId(),
                    "졸업자 리뷰"
            );

    @Test
    @DisplayName("스터디 졸업자가 아니면 리뷰를 작성할 수 없다")
    void throwExceptionByMemberIsNotGraduated() {
        // given
        given(studyParticipantRepository.isGraduatedParticipant(any(), any())).willReturn(false);

        // when - then
        assertThatThrownBy(() -> writeStudyReviewService.writeStudyReview(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyReviewErrorCode.ONLY_GRADUATED_PARTICIPANT_CAN_WRITE_REVIEW.getMessage());

        verify(studyParticipantRepository, times(1)).isGraduatedParticipant(any(), any());
        verify(studyReviewRepository, times(0)).existsByStudyIdAndWriterId(any(), any());
        verify(studyReviewRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("이미 리뷰를 작성했다면 더이상 작성할 수 없다")
    void throwExceptionByMemberIsAlreadyWrittenReview() {
        // given
        given(studyParticipantRepository.isGraduatedParticipant(any(), any())).willReturn(true);
        given(studyReviewRepository.existsByStudyIdAndWriterId(any(), any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> writeStudyReviewService.writeStudyReview(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyReviewErrorCode.ALREADY_WRITTEN.getMessage());

        verify(studyParticipantRepository, times(1)).isGraduatedParticipant(any(), any());
        verify(studyReviewRepository, times(1)).existsByStudyIdAndWriterId(any(), any());
        verify(studyReviewRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("스터디 리뷰를 작성한다")
    void success() {
        // given
        given(studyParticipantRepository.isGraduatedParticipant(any(), any())).willReturn(true);
        given(studyReviewRepository.existsByStudyIdAndWriterId(any(), any())).willReturn(false);

        final StudyReview review = StudyReview.writeReview(
                study.getId(),
                member.getId(),
                "졸업자 리뷰"
        ).apply(1L, LocalDateTime.now());
        given(studyReviewRepository.save(any())).willReturn(review);

        // when
        final Long studyReviewId = writeStudyReviewService.writeStudyReview(command);

        // then
        assertThat(studyReviewId).isEqualTo(review.getId());
        verify(studyParticipantRepository, times(1)).isGraduatedParticipant(any(), any());
        verify(studyReviewRepository, times(1)).existsByStudyIdAndWriterId(any(), any());
        verify(studyReviewRepository, times(1)).save(any());
    }
}
