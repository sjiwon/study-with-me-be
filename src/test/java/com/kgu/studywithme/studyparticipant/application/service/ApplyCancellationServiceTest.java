package com.kgu.studywithme.studyparticipant.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApplyCancellationUseCase;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
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

@DisplayName("StudyParticipant -> ApplyCancellationService 테스트")
class ApplyCancellationServiceTest extends UseCaseTest {
    @InjectMocks
    private ApplyCancellationService applyCancellationService;

    @Mock
    private StudyParticipantRepository studyParticipantRepository;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member applier = GHOST.toMember().apply(2L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
    private final StudyParticipant participant = StudyParticipant.applyInStudy(study.getId(), applier.getId())
            .apply(1L, LocalDateTime.now());
    private final ApplyCancellationUseCase.Command command = new ApplyCancellationUseCase.Command(study.getId(), participant.getId());

    @Test
    @DisplayName("스터디 신청자가 아니면 신청 취소를 할 수 없다")
    void throwExceptionByRequesterIsNotApplier() {
        // given
        given(studyParticipantRepository.findParticipantByStatus(any(), any(), any())).willReturn(Optional.empty());

        // when - then
        assertThatThrownBy(() -> applyCancellationService.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.APPLIER_NOT_FOUND.getMessage());

        assertAll(
                () -> verify(studyParticipantRepository, times(1)).findParticipantByStatus(any(), any(), any()),
                () -> verify(studyParticipantRepository, times(0)).delete(any())
        );
    }

    @Test
    @DisplayName("스터디 참여 신청한 내역을 취소한다")
    void success() {
        // given
        given(studyParticipantRepository.findParticipantByStatus(any(), any(), any())).willReturn(Optional.of(participant));

        // when
        applyCancellationService.invoke(command);

        // then
        assertAll(
                () -> verify(studyParticipantRepository, times(1)).findParticipantByStatus(any(), any(), any()),
                () -> verify(studyParticipantRepository, times(1)).delete(any())
        );
    }
}
