package com.kgu.studywithme.studyparticipant.infrastructure.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.infrastructure.persistence.MemberJpaRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.infrastructure.persistence.StudyJpaRepository;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyParticipant -> ParticipantHandlingRepository 테스트")
class ParticipantHandlingRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyParticipantRepository studyParticipantRepository;

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
    @DisplayName("특정 스터디 신청자를 조회한다 [With studyId + memberId]")
    void findApplier() {
        assertAll(
                () -> assertThat(studyParticipantRepository.findApplier(study.getId(), host.getId())).isEmpty(),
                () -> assertThat(studyParticipantRepository.findApplier(study.getId(), applier.getId())).isPresent(),
                () -> assertThat(studyParticipantRepository.findApplier(study.getId(), participant.getId())).isEmpty(),
                () -> assertThat(studyParticipantRepository.findApplier(study.getId(), leaveMember.getId())).isEmpty(),
                () -> assertThat(studyParticipantRepository.findApplier(study.getId(), graduatedMember.getId())).isEmpty()
        );
    }

    @Test
    @DisplayName("특정 스터디 참여자를 조회한다 [With studyId + memberId]")
    void findParticipant() {
        assertAll(
                () -> assertThat(studyParticipantRepository.findParticipant(study.getId(), host.getId())).isPresent(),
                () -> assertThat(studyParticipantRepository.findParticipant(study.getId(), applier.getId())).isEmpty(),
                () -> assertThat(studyParticipantRepository.findParticipant(study.getId(), participant.getId())).isPresent(),
                () -> assertThat(studyParticipantRepository.findParticipant(study.getId(), leaveMember.getId())).isEmpty(),
                () -> assertThat(studyParticipantRepository.findParticipant(study.getId(), graduatedMember.getId())).isEmpty()
        );
    }

    @Test
    @DisplayName("스터디 참여자들 ID(PK)를 조회한다 [With studyId]")
    void findStudyParticipantIds() {
        /* host + participant */
        final List<Long> studyParticipantIds1 = studyParticipantRepository.findStudyParticipantIds(study.getId());
        assertAll(
                () -> assertThat(studyParticipantIds1).hasSize(2),
                () -> assertThat(studyParticipantIds1).containsExactlyInAnyOrder(host.getId(), participant.getId())
        );

        /* + applier */
        studyParticipantRepository.updateParticipantStatus(study.getId(), applier.getId(), APPROVE);
        final List<Long> studyParticipantIds2 = studyParticipantRepository.findStudyParticipantIds(study.getId());
        assertAll(
                () -> assertThat(studyParticipantIds2).hasSize(3),
                () -> assertThat(studyParticipantIds2).containsExactlyInAnyOrder(host.getId(), participant.getId(), applier.getId())
        );

        /* - host & participant */
        studyParticipantRepository.updateParticipantStatus(study.getId(), host.getId(), GRADUATED);
        studyParticipantRepository.updateParticipantStatus(study.getId(), participant.getId(), GRADUATED);
        final List<Long> studyParticipantIds3 = studyParticipantRepository.findStudyParticipantIds(study.getId());
        assertAll(
                () -> assertThat(studyParticipantIds3).hasSize(1),
                () -> assertThat(studyParticipantIds3).containsExactlyInAnyOrder(applier.getId())
        );
    }

    @Test
    @DisplayName("스터디 신청 취소에 의해 신청자 정보를 삭제한다")
    void deleteApplier() {
        assertAll(
                () -> assertThat(studyParticipantRepository.deleteApplier(study.getId(), host.getId())).isEqualTo(0),
                () -> assertThat(studyParticipantRepository.deleteApplier(study.getId(), applier.getId())).isEqualTo(1),
                () -> assertThat(studyParticipantRepository.deleteApplier(study.getId(), participant.getId())).isEqualTo(0),
                () -> assertThat(studyParticipantRepository.deleteApplier(study.getId(), leaveMember.getId())).isEqualTo(0),
                () -> assertThat(studyParticipantRepository.deleteApplier(study.getId(), graduatedMember.getId())).isEqualTo(0)
        );
    }
}
