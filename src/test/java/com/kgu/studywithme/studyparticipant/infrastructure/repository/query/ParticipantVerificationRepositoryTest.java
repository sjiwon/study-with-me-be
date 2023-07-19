package com.kgu.studywithme.studyparticipant.infrastructure.repository.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyParticipant -> ParticipantVerificationRepository 테스트")
class ParticipantVerificationRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyParticipantRepository studyParticipantRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    private Member host;
    private Member applier;
    private Member participant;
    private Member leaveMember;
    private Member graduatedMember;
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        applier = memberRepository.save(DUMMY1.toMember());
        participant = memberRepository.save(DUMMY2.toMember());
        leaveMember = memberRepository.save(DUMMY3.toMember());
        graduatedMember = memberRepository.save(DUMMY4.toMember());

        study = studyRepository.save(SPRING.toOnlineStudy(host.getId()));

        studyParticipantRepository.save(
                StudyParticipant.applyHost(study.getId(), host.getId())
        );
        studyParticipantRepository.save(
                StudyParticipant.applyInStudy(study.getId(), applier.getId())
        );
        studyParticipantRepository.save(
                StudyParticipant.applyParticipant(study.getId(), participant.getId(), APPROVE)
        );
        studyParticipantRepository.save(
                StudyParticipant.applyParticipant(study.getId(), leaveMember.getId(), LEAVE)
        );
        studyParticipantRepository.save(
                StudyParticipant.applyParticipant(study.getId(), graduatedMember.getId(), GRADUATED)
        );
    }

    @Test
    @DisplayName("스터디 신청자인지 확인한다 (참여 상태 = APPLY)")
    void isApplier() {
        assertAll(
                () -> assertThat(
                        studyParticipantRepository.isApplier(study.getId(), host.getId())
                ).isFalse(),
                () -> assertThat(
                        studyParticipantRepository.isApplier(study.getId(), applier.getId())
                ).isTrue(),
                () -> assertThat(
                        studyParticipantRepository.isApplier(study.getId(), participant.getId())
                ).isFalse(),
                () -> assertThat(
                        studyParticipantRepository.isApplier(study.getId(), leaveMember.getId())
                ).isFalse(),
                () -> assertThat(
                        studyParticipantRepository.isApplier(study.getId(), graduatedMember.getId())
                ).isFalse()
        );
    }

    @Test
    @DisplayName("스터디 참여자인지 확인한다 (참여 상태 = APPROVE)")
    void isParticipant() {
        assertAll(
                () -> assertThat(
                        studyParticipantRepository.isParticipant(study.getId(), host.getId())
                ).isTrue(),
                () -> assertThat(
                        studyParticipantRepository.isParticipant(study.getId(), applier.getId())
                ).isFalse(),
                () -> assertThat(
                        studyParticipantRepository.isParticipant(study.getId(), participant.getId())
                ).isTrue(),
                () -> assertThat(
                        studyParticipantRepository.isParticipant(study.getId(), leaveMember.getId())
                ).isFalse(),
                () -> assertThat(
                        studyParticipantRepository.isParticipant(study.getId(), graduatedMember.getId())
                ).isFalse()
        );
    }

    @Test
    @DisplayName("스터디 신청자 or 참여자인지 확인한다 (참여 상태 IN APPLY, APPROVE)")
    void isApplierOrParticipant() {
        assertAll(
                () -> assertThat(
                        studyParticipantRepository.isApplierOrParticipant(study.getId(), host.getId())
                ).isTrue(),
                () -> assertThat(
                        studyParticipantRepository.isApplierOrParticipant(study.getId(), applier.getId())
                ).isTrue(),
                () -> assertThat(
                        studyParticipantRepository.isApplierOrParticipant(study.getId(), participant.getId())
                ).isTrue(),
                () -> assertThat(
                        studyParticipantRepository.isApplierOrParticipant(study.getId(), leaveMember.getId())
                ).isFalse(),
                () -> assertThat(
                        studyParticipantRepository.isApplierOrParticipant(study.getId(), graduatedMember.getId())
                ).isFalse()
        );
    }

    @Test
    @DisplayName("스터디 졸업자인지 확인한다 (참여 상태 = GRADUATED)")
    void isGraduatedParticipant() {
        assertAll(
                () -> assertThat(
                        studyParticipantRepository.isGraduatedParticipant(study.getId(), host.getId())
                ).isFalse(),
                () -> assertThat(
                        studyParticipantRepository.isGraduatedParticipant(study.getId(), applier.getId())
                ).isFalse(),
                () -> assertThat(
                        studyParticipantRepository.isGraduatedParticipant(study.getId(), participant.getId())
                ).isFalse(),
                () -> assertThat(
                        studyParticipantRepository.isGraduatedParticipant(study.getId(), leaveMember.getId())
                ).isFalse(),
                () -> assertThat(
                        studyParticipantRepository.isGraduatedParticipant(study.getId(), graduatedMember.getId())
                ).isTrue()
        );
    }

    @Test
    @DisplayName("스터디 참여 취소자 or 졸업자인지 확인한다 (참여 상태 IN LEAVE, GRADUATED)")
    void isAlreadyLeaveOrGraduatedParticipant() {
        assertAll(
                () -> assertThat(
                        studyParticipantRepository.isAlreadyLeaveOrGraduatedParticipant(study.getId(), host.getId())
                ).isFalse(),
                () -> assertThat(
                        studyParticipantRepository.isAlreadyLeaveOrGraduatedParticipant(study.getId(), applier.getId())
                ).isFalse(),
                () -> assertThat(
                        studyParticipantRepository.isAlreadyLeaveOrGraduatedParticipant(study.getId(), participant.getId())
                ).isFalse(),
                () -> assertThat(
                        studyParticipantRepository.isAlreadyLeaveOrGraduatedParticipant(study.getId(), leaveMember.getId())
                ).isTrue(),
                () -> assertThat(
                        studyParticipantRepository.isAlreadyLeaveOrGraduatedParticipant(study.getId(), graduatedMember.getId())
                ).isTrue()
        );
    }
}
