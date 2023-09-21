package com.kgu.studywithme.studyreview.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyreview.application.usecase.command.WriteStudyReviewCommand;
import com.kgu.studywithme.studyreview.domain.model.StudyReview;
import com.kgu.studywithme.studyreview.domain.repository.StudyReviewRepository;
import com.kgu.studywithme.studyreview.exception.StudyReviewErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyReview -> WriteStudyReviewUseCase 테스트")
class WriteStudyReviewUseCaseTest extends UseCaseTest {
    private final StudyParticipantRepository studyParticipantRepository = mock(StudyParticipantRepository.class);
    private final StudyReviewRepository studyReviewRepository = mock(StudyReviewRepository.class);
    private final WriteStudyReviewUseCase sut = new WriteStudyReviewUseCase(studyParticipantRepository, studyReviewRepository);

    private final Member host = JIWON.toMember().apply(1L);
    private final Member member = GHOST.toMember().apply(2L);
    private final Study study = SPRING.toStudy(host.getId()).apply(1L);
    private final WriteStudyReviewCommand command = new WriteStudyReviewCommand(study.getId(), member.getId(), "졸업자 리뷰");

    @Test
    @DisplayName("스터디 졸업자가 아니면 리뷰를 작성할 수 없다")
    void throwExceptionByMemberIsNotGraduated() {
        // given
        given(studyParticipantRepository.isGraduatedParticipant(command.studyId(), command.memberId())).willReturn(false);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyReviewErrorCode.ONLY_GRADUATED_PARTICIPANT_CAN_WRITE_REVIEW.getMessage());

        assertAll(
                () -> verify(studyParticipantRepository, times(1)).isGraduatedParticipant(command.studyId(), command.memberId()),
                () -> verify(studyReviewRepository, times(0)).existsByStudyIdAndWriterId(command.studyId(), command.memberId()),
                () -> verify(studyReviewRepository, times(0)).save(any(StudyReview.class))
        );
    }

    @Test
    @DisplayName("이미 리뷰를 작성했다면 추가 작성할 수 없다")
    void throwExceptionByMemberIsAlreadyWrittenReview() {
        // given
        given(studyParticipantRepository.isGraduatedParticipant(command.studyId(), command.memberId())).willReturn(true);
        given(studyReviewRepository.existsByStudyIdAndWriterId(command.studyId(), command.memberId())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyReviewErrorCode.ALREADY_WRITTEN.getMessage());

        assertAll(
                () -> verify(studyParticipantRepository, times(1)).isGraduatedParticipant(command.studyId(), command.memberId()),
                () -> verify(studyReviewRepository, times(1)).existsByStudyIdAndWriterId(command.studyId(), command.memberId()),
                () -> verify(studyReviewRepository, times(0)).save(any(StudyReview.class))
        );
    }

    @Test
    @DisplayName("스터디 리뷰를 작성한다")
    void success() {
        // given
        given(studyParticipantRepository.isGraduatedParticipant(command.studyId(), command.memberId())).willReturn(true);
        given(studyReviewRepository.existsByStudyIdAndWriterId(command.studyId(), command.memberId())).willReturn(false);

        final StudyReview review = command.toDomain().apply(1L);
        given(studyReviewRepository.save(any(StudyReview.class))).willReturn(review);

        // when
        final Long studyReviewId = sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyParticipantRepository, times(1)).isGraduatedParticipant(command.studyId(), command.memberId()),
                () -> verify(studyReviewRepository, times(1)).existsByStudyIdAndWriterId(command.studyId(), command.memberId()),
                () -> verify(studyReviewRepository, times(1)).save(any(StudyReview.class)),
                () -> assertThat(studyReviewId).isEqualTo(review.getId())
        );
    }
}
