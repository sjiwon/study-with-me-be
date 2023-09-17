package com.kgu.studywithme.studyparticipant.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.application.adapter.ParticipateMemberReadAdapter;
import com.kgu.studywithme.studyparticipant.application.usecase.command.GraduateStudyUseCase;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.event.StudyGraduatedEvent;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static com.kgu.studywithme.common.fixture.MemberFixture.ANONYMOUS;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyParticipant -> GraduateStudyService 테스트")
class GraduateStudyServiceTest extends UseCaseTest {
    @InjectMocks
    private GraduateStudyService graduateStudyService;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private ParticipateMemberReadAdapter participateMemberReadAdapter;

    @Mock
    private StudyParticipantRepository studyParticipantRepository;

    @Mock
    private StudyAttendanceRepository studyAttendanceRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member applierWithAllowEmail = GHOST.toMember().apply(2L, LocalDateTime.now());
    private final Member applierWithNotAllowEmail = ANONYMOUS.toMember().apply(3L, LocalDateTime.now());
    private Study study;
    private int previousParticipantMembers;

    @BeforeEach
    void setUp() {
        study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
        previousParticipantMembers = study.getParticipants();
    }

    @Test
    @DisplayName("스터디 팀장은 팀장 권한을 위임하지 않으면 스터디를 졸업할 수 없다")
    void throwExceptionByHostCannotGraduateStudy() {
        // given
        given(studyRepository.getById(any())).willReturn(study);

        // when - then
        assertThatThrownBy(() -> graduateStudyService.invoke(new GraduateStudyUseCase.Command(study.getId(), host.getId())))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.HOST_CANNOT_GRADUATE_STUDY.getMessage());

        assertAll(
                () -> verify(studyRepository, times(1)).getById(any()),
                () -> verify(participateMemberReadAdapter, times(0)).getParticipant(any(), any()),
                () -> verify(studyAttendanceRepository, times(0)).getAttendanceStatusCount(any(), any()),
                () -> verify(studyParticipantRepository, times(0)).updateParticipantStatus(any(), any(), any()),
                () -> verify(eventPublisher, times(0)).publishEvent(any(StudyGraduatedEvent.class))
        );
    }

    @Test
    @DisplayName("참여자가 아닌 사람은 해당 스터디를 졸업할 수 없다")
    void throwExceptionByParticipantNotFound() {
        // given
        given(studyRepository.getById(any())).willReturn(study);
        doThrow(StudyWithMeException.type(StudyParticipantErrorCode.PARTICIPANT_NOT_FOUND))
                .when(participateMemberReadAdapter)
                .getParticipant(any(), any());

        // when - then
        assertThatThrownBy(() -> graduateStudyService.invoke(new GraduateStudyUseCase.Command(study.getId(), applierWithAllowEmail.getId())))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.PARTICIPANT_NOT_FOUND.getMessage());

        assertAll(
                () -> verify(studyRepository, times(1)).getById(any()),
                () -> verify(participateMemberReadAdapter, times(1)).getParticipant(any(), any()),
                () -> verify(studyAttendanceRepository, times(0)).getAttendanceStatusCount(any(), any()),
                () -> verify(studyParticipantRepository, times(0)).updateParticipantStatus(any(), any(), any()),
                () -> verify(eventPublisher, times(0)).publishEvent(any(StudyGraduatedEvent.class))
        );
    }

    @Test
    @DisplayName("졸업 요건을 만족하지 못한 참여자는 스터디를 졸업할 수 없다")
    void throwExceptionByParticipantNotMeetGraduationPolicy() {
        // given
        given(studyRepository.getById(any())).willReturn(study);
        given(participateMemberReadAdapter.getParticipant(any(), any())).willReturn(applierWithAllowEmail);
        given(studyAttendanceRepository.getAttendanceStatusCount(any(), any()))
                .willReturn(study.getGraduationPolicy().getMinimumAttendance() - 1);

        // when - then
        assertThatThrownBy(() -> graduateStudyService.invoke(new GraduateStudyUseCase.Command(study.getId(), applierWithAllowEmail.getId())))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyParticipantErrorCode.PARTICIPANT_NOT_MEET_GRADUATION_POLICY.getMessage());

        assertAll(
                () -> verify(studyRepository, times(1)).getById(any()),
                () -> verify(participateMemberReadAdapter, times(1)).getParticipant(any(), any()),
                () -> verify(studyAttendanceRepository, times(0)).getAttendanceStatusCount(any(), any()),
                () -> verify(studyParticipantRepository, times(0)).updateParticipantStatus(any(), any(), any()),
                () -> verify(eventPublisher, times(0)).publishEvent(any(StudyGraduatedEvent.class))
        );
    }

    @Test
    @DisplayName("스터디를 졸업한다 [이메일 수신 동의에 의한 이메일 발송 이벤트 O]")
    void successA() {
        // given
        given(studyRepository.getById(any())).willReturn(study);
        given(participateMemberReadAdapter.getParticipant(any(), any())).willReturn(applierWithAllowEmail);
        given(studyAttendanceRepository.getAttendanceStatusCount(any(), any()))
                .willReturn(study.getGraduationPolicy().getMinimumAttendance());

        // when
        graduateStudyService.invoke(new GraduateStudyUseCase.Command(study.getId(), applierWithAllowEmail.getId()));

        // then
        assertAll(
                () -> verify(studyRepository, times(1)).getById(any()),
                () -> verify(participateMemberReadAdapter, times(1)).getParticipant(any(), any()),
                () -> verify(studyAttendanceRepository, times(0)).getAttendanceStatusCount(any(), any()),
                () -> verify(studyParticipantRepository, times(1)).updateParticipantStatus(any(), any(), any()),
                () -> verify(eventPublisher, times(1)).publishEvent(any(StudyGraduatedEvent.class)),
                () -> assertThat(study.getParticipants()).isEqualTo(previousParticipantMembers - 1)
        );
    }

    @Test
    @DisplayName("스터디를 졸업한다 [이메일 수신 비동의에 의한 이메일 발송 이벤트 X]")
    void successB() {
        // given
        given(studyRepository.getById(any())).willReturn(study);
        given(participateMemberReadAdapter.getParticipant(any(), any())).willReturn(applierWithNotAllowEmail);
        given(studyAttendanceRepository.getAttendanceStatusCount(any(), any()))
                .willReturn(study.getGraduationPolicy().getMinimumAttendance());

        // when
        graduateStudyService.invoke(new GraduateStudyUseCase.Command(study.getId(), applierWithNotAllowEmail.getId()));

        // then
        assertAll(
                () -> verify(studyRepository, times(1)).getById(any()),
                () -> verify(participateMemberReadAdapter, times(1)).getParticipant(any(), any()),
                () -> verify(studyAttendanceRepository, times(0)).getAttendanceStatusCount(any(), any()),
                () -> verify(studyParticipantRepository, times(1)).updateParticipantStatus(any(), any(), any()),
                () -> verify(eventPublisher, times(0)).publishEvent(any(StudyGraduatedEvent.class)),
                () -> assertThat(study.getParticipants()).isEqualTo(previousParticipantMembers - 1)
        );
    }
}
