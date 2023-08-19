package com.kgu.studywithme.studyparticipant.infrastructure.persistence;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.infrastructure.persistence.MemberJpaRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.infrastructure.persistence.StudyJpaRepository;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipant;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY1;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY2;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY3;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY4;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.GRADUATED;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.LEAVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(ParticipantReader.class)
@DisplayName("StudyParticipant -> ParticipantReader 테스트")
public class ParticipantReaderTest extends RepositoryTest {
    @Autowired
    private ParticipantReader participantReader;

    @Autowired
    private StudyParticipantJpaRepository studyParticipantJpaRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private StudyJpaRepository studyJpaRepository;

    private Member host;
    private Member applier;
    private Member participant;
    private Member leaveMember;
    private Member graduatedMember;
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberJpaRepository.save(JIWON.toMember());
        applier = memberJpaRepository.save(DUMMY1.toMember());
        participant = memberJpaRepository.save(DUMMY2.toMember());
        leaveMember = memberJpaRepository.save(DUMMY3.toMember());
        graduatedMember = memberJpaRepository.save(DUMMY4.toMember());

        study = studyJpaRepository.save(SPRING.toOnlineStudy(host.getId()));
        studyParticipantJpaRepository.saveAll(
                List.of(
                        StudyParticipant.applyHost(study.getId(), host.getId()),
                        StudyParticipant.applyInStudy(study.getId(), applier.getId()),
                        StudyParticipant.applyParticipant(study.getId(), participant.getId(), APPROVE),
                        StudyParticipant.applyParticipant(study.getId(), leaveMember.getId(), LEAVE),
                        StudyParticipant.applyParticipant(study.getId(), graduatedMember.getId(), GRADUATED)
                )
        );
    }

    @Test
    @DisplayName("특정 스터디 신청자를 조회한다 [With studyId + memberId]")
    void getApplier() {
        assertAll(
                () -> assertThatThrownBy(() -> participantReader.getApplier(study.getId(), host.getId()))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyParticipantErrorCode.APPLIER_NOT_FOUND.getMessage()),
                () -> assertThatThrownBy(() -> participantReader.getApplier(study.getId(), participant.getId()))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyParticipantErrorCode.APPLIER_NOT_FOUND.getMessage()),
                () -> assertThatThrownBy(() -> participantReader.getApplier(study.getId(), leaveMember.getId()))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyParticipantErrorCode.APPLIER_NOT_FOUND.getMessage()),
                () -> assertThatThrownBy(() -> participantReader.getApplier(study.getId(), graduatedMember.getId()))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyParticipantErrorCode.APPLIER_NOT_FOUND.getMessage()),
                () -> assertThat(participantReader.getApplier(study.getId(), applier.getId())).isEqualTo(applier)
        );
    }

    @Test
    @DisplayName("특정 스터디 참여자를 조회한다 [With studyId + memberId]")
    void getParticipant() {
        assertAll(
                () -> assertThatThrownBy(() -> participantReader.getParticipant(study.getId(), applier.getId()))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyParticipantErrorCode.PARTICIPANT_NOT_FOUND.getMessage()),
                () -> assertThatThrownBy(() -> participantReader.getParticipant(study.getId(), leaveMember.getId()))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyParticipantErrorCode.PARTICIPANT_NOT_FOUND.getMessage()),
                () -> assertThatThrownBy(() -> participantReader.getParticipant(study.getId(), graduatedMember.getId()))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyParticipantErrorCode.PARTICIPANT_NOT_FOUND.getMessage()),
                () -> assertThat(participantReader.getParticipant(study.getId(), host.getId())).isEqualTo(host),
                () -> assertThat(participantReader.getParticipant(study.getId(), participant.getId())).isEqualTo(participant)
        );
    }
}
