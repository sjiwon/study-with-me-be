package com.kgu.studywithme.studyparticipant.infrastructure.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyparticipant.domain.model.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
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
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.GRADUATED;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.LEAVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(ParticipantVerificationRepository.class)
@DisplayName("StudyParticipant -> ParticipantVerificationRepository 테스트")
class ParticipantVerificationRepositoryTest extends RepositoryTest {
    @Autowired
    private ParticipantVerificationRepository participantVerificationRepository;

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
        studyParticipantRepository.saveAll(
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
    @DisplayName("스터디 참여자인지 확인한다 (참여 상태 = APPROVE)")
    void isParticipant() {
        assertAll(
                () -> assertThat(participantVerificationRepository.isParticipant(study.getId(), host.getId())).isTrue(),
                () -> assertThat(participantVerificationRepository.isParticipant(study.getId(), applier.getId())).isFalse(),
                () -> assertThat(participantVerificationRepository.isParticipant(study.getId(), participant.getId())).isTrue(),
                () -> assertThat(participantVerificationRepository.isParticipant(study.getId(), leaveMember.getId())).isFalse(),
                () -> assertThat(participantVerificationRepository.isParticipant(study.getId(), graduatedMember.getId())).isFalse()
        );
    }

    @Test
    @DisplayName("스터디 졸업자인지 확인한다 (참여 상태 = GRADUATED)")
    void isGraduatedParticipant() {
        assertAll(
                () -> assertThat(participantVerificationRepository.isGraduatedParticipant(study.getId(), host.getId())).isFalse(),
                () -> assertThat(participantVerificationRepository.isGraduatedParticipant(study.getId(), applier.getId())).isFalse(),
                () -> assertThat(participantVerificationRepository.isGraduatedParticipant(study.getId(), participant.getId())).isFalse(),
                () -> assertThat(participantVerificationRepository.isGraduatedParticipant(study.getId(), leaveMember.getId())).isFalse(),
                () -> assertThat(participantVerificationRepository.isGraduatedParticipant(study.getId(), graduatedMember.getId())).isTrue()
        );
    }

    @Test
    @DisplayName("스터디 신청자 or 참여자인지 확인한다 (참여 상태 IN APPLY, APPROVE)")
    void isApplierOrParticipant() {
        assertAll(
                () -> assertThat(participantVerificationRepository.isApplierOrParticipant(study.getId(), host.getId())).isTrue(),
                () -> assertThat(participantVerificationRepository.isApplierOrParticipant(study.getId(), applier.getId())).isTrue(),
                () -> assertThat(participantVerificationRepository.isApplierOrParticipant(study.getId(), participant.getId())).isTrue(),
                () -> assertThat(participantVerificationRepository.isApplierOrParticipant(study.getId(), leaveMember.getId())).isFalse(),
                () -> assertThat(participantVerificationRepository.isApplierOrParticipant(study.getId(), graduatedMember.getId())).isFalse()
        );
    }

    @Test
    @DisplayName("스터디 참여 취소자 or 졸업자인지 확인한다 (참여 상태 IN LEAVE, GRADUATED)")
    void isAlreadyLeaveOrGraduatedParticipant() {
        assertAll(
                () -> assertThat(participantVerificationRepository.isAlreadyLeaveOrGraduatedParticipant(study.getId(), host.getId())).isFalse(),
                () -> assertThat(participantVerificationRepository.isAlreadyLeaveOrGraduatedParticipant(study.getId(), applier.getId())).isFalse(),
                () -> assertThat(participantVerificationRepository.isAlreadyLeaveOrGraduatedParticipant(study.getId(), participant.getId())).isFalse(),
                () -> assertThat(participantVerificationRepository.isAlreadyLeaveOrGraduatedParticipant(study.getId(), leaveMember.getId())).isTrue(),
                () -> assertThat(participantVerificationRepository.isAlreadyLeaveOrGraduatedParticipant(study.getId(), graduatedMember.getId())).isTrue()
        );
    }
}
