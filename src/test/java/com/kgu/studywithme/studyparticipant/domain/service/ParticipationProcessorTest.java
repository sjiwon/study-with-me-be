package com.kgu.studywithme.studyparticipant.domain.service;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyparticipant.domain.event.StudyApprovedEvent;
import com.kgu.studywithme.studyparticipant.domain.event.StudyGraduatedEvent;
import com.kgu.studywithme.studyparticipant.domain.event.StudyRejectedEvent;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static com.kgu.studywithme.common.fixture.MemberFixture.ANONYMOUS;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.GRADUATED;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.LEAVE;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.REJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyParticipant -> ParticipationProcessor 테스트")
public class ParticipationProcessorTest extends ParallelTest {
    private final StudyParticipantRepository studyParticipantRepository = mock(StudyParticipantRepository.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final ParticipationProcessor sut = new ParticipationProcessor(studyParticipantRepository, eventPublisher);

    private final Member host = JIWON.toMember().apply(1L);
    private final Member memberWithAllowEmail = GHOST.toMember().apply(2L);
    private final Member memberWithNotAllowEmail = ANONYMOUS.toMember().apply(3L);
    private Study study;
    private int previousParticipantMembers;

    @BeforeEach
    void setUp() {
        study = SPRING.toStudy(host.getId()).apply(1L);
        previousParticipantMembers = study.getParticipants();
    }

    @Nested
    @DisplayName("스터디 신청자에 대한 참여를 승인한다")
    class ApproveApplier {
        @Test
        @DisplayName("이메일 발송 O")
        void successWithEmail() {
            // when
            sut.approveApplier(study, memberWithAllowEmail);

            // then
            assertAll(
                    () -> verify(studyParticipantRepository, times(1))
                            .updateParticipantStatus(study.getId(), memberWithAllowEmail.getId(), APPROVE),
                    () -> verify(eventPublisher, times(1)).publishEvent(any(StudyApprovedEvent.class)),
                    () -> assertThat(study.getParticipants()).isEqualTo(previousParticipantMembers + 1)
            );
        }

        @Test
        @DisplayName("이메일 발송 X")
        void successWithNoEmail() {
            // when
            sut.approveApplier(study, memberWithNotAllowEmail);

            // then
            assertAll(
                    () -> verify(studyParticipantRepository, times(1))
                            .updateParticipantStatus(study.getId(), memberWithNotAllowEmail.getId(), APPROVE),
                    () -> verify(eventPublisher, times(0)).publishEvent(any(StudyApprovedEvent.class)),
                    () -> assertThat(study.getParticipants()).isEqualTo(previousParticipantMembers + 1)
            );
        }
    }

    @Nested
    @DisplayName("스터디 신청자에 대한 참여를 거절한다")
    class RejectApplier {
        @Test
        @DisplayName("이메일 발송 O")
        void successWithEmail() {
            // when
            sut.rejectApplier(study, memberWithAllowEmail, "sorry");

            // then
            assertAll(
                    () -> verify(studyParticipantRepository, times(1))
                            .updateParticipantStatus(study.getId(), memberWithAllowEmail.getId(), REJECT),
                    () -> verify(eventPublisher, times(1)).publishEvent(any(StudyRejectedEvent.class))
            );
        }

        @Test
        @DisplayName("이메일 발송 X")
        void successWithNoEmail() {
            // when
            sut.rejectApplier(study, memberWithNotAllowEmail, "sorry");

            // then
            assertAll(
                    () -> verify(studyParticipantRepository, times(1))
                            .updateParticipantStatus(study.getId(), memberWithNotAllowEmail.getId(), REJECT),
                    () -> verify(eventPublisher, times(0)).publishEvent(any(StudyRejectedEvent.class))
            );
        }
    }

    @Test
    @DisplayName("스터디를 떠난다")
    void leaveStudy() {
        // when
        sut.leaveStudy(study, memberWithAllowEmail);

        // then
        assertAll(
                () -> verify(studyParticipantRepository, times(1))
                        .updateParticipantStatus(study.getId(), memberWithAllowEmail.getId(), LEAVE),
                () -> assertThat(study.getParticipants()).isEqualTo(previousParticipantMembers - 1)
        );
    }

    @Nested
    @DisplayName("스터디를 졸업한다")
    class GraduateStudy {
        @Test
        @DisplayName("이메일 발송 O")
        void successWithEmail() {
            // when
            sut.graduateStudy(study, memberWithAllowEmail);

            // then
            assertAll(
                    () -> verify(studyParticipantRepository, times(1))
                            .updateParticipantStatus(study.getId(), memberWithAllowEmail.getId(), GRADUATED),
                    () -> verify(eventPublisher, times(1)).publishEvent(any(StudyGraduatedEvent.class)),
                    () -> assertThat(study.getParticipants()).isEqualTo(previousParticipantMembers - 1)
            );
        }

        @Test
        @DisplayName("이메일 발송 X")
        void successWithNoEmail() {
            // when
            sut.graduateStudy(study, memberWithNotAllowEmail);

            // then
            assertAll(
                    () -> verify(studyParticipantRepository, times(1))
                            .updateParticipantStatus(study.getId(), memberWithNotAllowEmail.getId(), GRADUATED),
                    () -> verify(eventPublisher, times(0)).publishEvent(any(StudyGraduatedEvent.class)),
                    () -> assertThat(study.getParticipants()).isEqualTo(previousParticipantMembers - 1)
            );
        }
    }
}
