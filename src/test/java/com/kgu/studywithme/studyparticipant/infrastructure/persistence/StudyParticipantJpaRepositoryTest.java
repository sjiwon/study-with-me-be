package com.kgu.studywithme.studyparticipant.infrastructure.persistence;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.infrastructure.persistence.MemberJpaRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.infrastructure.persistence.StudyJpaRepository;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPLY;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.GRADUATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyParticipant -> StudyParticipantJpaRepository 테스트")
public class StudyParticipantJpaRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyParticipantJpaRepository studyParticipantJpaRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private StudyJpaRepository studyJpaRepository;

    private Member host;
    private Member applier;
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberJpaRepository.save(JIWON.toMember());
        applier = memberJpaRepository.save(GHOST.toMember());
        study = studyJpaRepository.save(SPRING.toOnlineStudy(host.getId()));

        studyParticipantJpaRepository.save(StudyParticipant.applyHost(study.getId(), host.getId()));
    }

    @Test
    @DisplayName("스터디 신청자를 조회한다")
    void findApplier() {
        studyParticipantJpaRepository.save(StudyParticipant.applyInStudy(study.getId(), applier.getId()));
        assertThat(studyParticipantJpaRepository.findApplier(study.getId(), applier.getId())).isPresent();

        studyParticipantJpaRepository.updateParticipantStatus(study.getId(), applier.getId(), APPROVE);
        assertThat(studyParticipantJpaRepository.findApplier(study.getId(), applier.getId())).isEmpty();
    }

    @Test
    @DisplayName("스터디 참여자들의 ID(PK)를 조회한다")
    void findStudyParticipantIds() {
        studyParticipantJpaRepository.save(StudyParticipant.applyInStudy(study.getId(), applier.getId()));

        final List<Long> studyParticipantIds1 = studyParticipantJpaRepository.findStudyParticipantIds(study.getId());
        assertAll(
                () -> assertThat(studyParticipantIds1).hasSize(1),
                () -> assertThat(studyParticipantIds1).containsExactlyInAnyOrder(host.getId())
        );

        /* applier participant */
        studyParticipantJpaRepository.updateParticipantStatus(study.getId(), applier.getId(), APPROVE);

        final List<Long> studyParticipantIds2 = studyParticipantJpaRepository.findStudyParticipantIds(study.getId());
        assertAll(
                () -> assertThat(studyParticipantIds2).hasSize(2),
                () -> assertThat(studyParticipantIds2).containsExactlyInAnyOrder(host.getId(), applier.getId())
        );
    }

    @Test
    @DisplayName("스터디 참여자 상태를 업데이트한다")
    void updateParticipantStatus() {
        // given
        final StudyParticipant participant = studyParticipantJpaRepository.save(StudyParticipant.applyInStudy(study.getId(), applier.getId()));

        final StudyParticipant findParticipant1 = studyParticipantJpaRepository.findById(participant.getId()).orElseThrow();
        assertThat(findParticipant1.getStatus()).isEqualTo(APPLY);

        /* applier -> APPROVE */
        studyParticipantJpaRepository.updateParticipantStatus(study.getId(), applier.getId(), APPROVE);

        final StudyParticipant findParticipant2 = studyParticipantJpaRepository.findById(participant.getId()).orElseThrow();
        assertThat(findParticipant2.getStatus()).isEqualTo(APPROVE);

        /* applier -> GRADUATED */
        studyParticipantJpaRepository.updateParticipantStatus(study.getId(), applier.getId(), GRADUATED);

        final StudyParticipant findParticipant3 = studyParticipantJpaRepository.findById(participant.getId()).orElseThrow();
        assertThat(findParticipant3.getStatus()).isEqualTo(GRADUATED);
    }
}
