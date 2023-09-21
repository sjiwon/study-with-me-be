package com.kgu.studywithme.studyparticipant.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApplyCancelCommand;
import com.kgu.studywithme.studyparticipant.domain.model.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyParticipant -> ApplyCancelUseCase 테스트")
class ApplyCancelUseCaseTest extends UseCaseTest {
    private final StudyParticipantRepository studyParticipantRepository = mock(StudyParticipantRepository.class);
    private final ApplyCancelUseCase sut = new ApplyCancelUseCase(studyParticipantRepository);

    private final Member host = JIWON.toMember().apply(1L);
    private final Member applyMember = GHOST.toMember().apply(2L);
    private final Study study = SPRING.toStudy(host.getId()).apply(1L);
    private final StudyParticipant applier = StudyParticipant.applyInStudy(study.getId(), applyMember.getId()).apply(1L);
    private final ApplyCancelCommand command = new ApplyCancelCommand(study.getId(), applyMember.getId());

    @Test
    @DisplayName("스터디 신청자가 아니면 신청 취소를 할 수 없다")
    void throwExceptionByRequesterIsNotApplier() {
        // given
        doThrow(StudyWithMeException.type(StudyParticipantErrorCode.APPLIER_NOT_FOUND))
                .when(studyParticipantRepository)
                .getApplier(command.studyId(), command.applierId());

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.APPLIER_NOT_FOUND.getMessage());

        assertAll(
                () -> verify(studyParticipantRepository, times(1)).getApplier(command.studyId(), command.applierId()),
                () -> verify(studyParticipantRepository, times(0)).delete(applier)
        );
    }

    @Test
    @DisplayName("스터디 참여 신청한 내역을 취소한다")
    void success() {
        // given
        given(studyParticipantRepository.getApplier(command.studyId(), command.applierId())).willReturn(applier);

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyParticipantRepository, times(1)).getApplier(command.studyId(), command.applierId()),
                () -> verify(studyParticipantRepository, times(1)).delete(applier)
        );
    }
}
