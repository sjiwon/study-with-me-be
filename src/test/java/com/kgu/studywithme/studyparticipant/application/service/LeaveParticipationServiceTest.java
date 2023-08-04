package com.kgu.studywithme.studyparticipant.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.application.service.QueryStudyByIdService;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyparticipant.application.usecase.command.LeaveParticipationUseCase;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyParticipant -> LeaveParticipationService 테스트")
class LeaveParticipationServiceTest extends UseCaseTest {
    @InjectMocks
    private LeaveParticipationService leaveParticipationService;

    @Mock
    private QueryStudyByIdService queryStudyByIdService;

    @Mock
    private StudyParticipantRepository studyParticipantRepository;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member participant = GHOST.toMember().apply(2L, LocalDateTime.now());
    private Study study;
    private int previousParticipantMembers;

    @BeforeEach
    void setUp() {
        study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
        previousParticipantMembers = study.getParticipantMembers();
    }

    @Test
    @DisplayName("스터디 팀장은 팀장 권한을 위임하지 않으면 스터디 참여를 취소할 수 없다")
    void throwExceptionByHostCannotLeaveStudy() {
        // given
        given(queryStudyByIdService.findById(any())).willReturn(study);

        // when - then
        assertThatThrownBy(() -> leaveParticipationService.leaveParticipation(
                new LeaveParticipationUseCase.Command(
                        study.getId(),
                        host.getId()
                )
        ))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.HOST_CANNOT_LEAVE_STUDY.getMessage());

        assertAll(
                () -> verify(queryStudyByIdService, times(1)).findById(any()),
                () -> verify(studyParticipantRepository, times(0)).updateParticipantStatus(any(), any(), any())
        );
    }

    @Test
    @DisplayName("스터디 참여를 취소한다")
    void success() {
        // given
        given(queryStudyByIdService.findById(any())).willReturn(study);

        // when
        leaveParticipationService.leaveParticipation(
                new LeaveParticipationUseCase.Command(
                        study.getId(),
                        participant.getId()
                )
        );

        // then
        assertAll(
                () -> verify(queryStudyByIdService, times(1)).findById(any()),
                () -> verify(studyParticipantRepository, times(1)).updateParticipantStatus(any(), any(), any()),
                () -> assertThat(study.getParticipantMembers()).isEqualTo(previousParticipantMembers - 1)
        );
    }
}
