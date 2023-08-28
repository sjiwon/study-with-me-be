package com.kgu.studywithme.studyparticipant.domain;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
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

@DisplayName("StudyParticipant -> StudyParticipantRepository 테스트")
public class StudyParticipantRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyParticipantRepository studyParticipantRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    private Member host;
    private Member applier;
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        applier = memberRepository.save(GHOST.toMember());
        study = studyRepository.save(SPRING.toOnlineStudy(host.getId()));

        studyParticipantRepository.save(StudyParticipant.applyHost(study.getId(), host.getId()));
    }

    @Test
    @DisplayName("스터디 참여자를 ParticipantStatus에 따라 조회한다")
    void findApplier() {
        studyParticipantRepository.save(StudyParticipant.applyInStudy(study.getId(), applier.getId()));
        assertAll(
                () -> assertThat(studyParticipantRepository.findParticipantByStatus(study.getId(), applier.getId(), APPLY)).isPresent(),
                () -> assertThat(studyParticipantRepository.findParticipantByStatus(study.getId(), applier.getId(), APPROVE)).isEmpty(),
                () -> assertThat(studyParticipantRepository.findParticipantByStatus(study.getId(), applier.getId(), GRADUATED)).isEmpty()
        );

        studyParticipantRepository.updateParticipantStatus(study.getId(), applier.getId(), APPROVE);
        assertAll(
                () -> assertThat(studyParticipantRepository.findParticipantByStatus(study.getId(), applier.getId(), APPLY)).isEmpty(),
                () -> assertThat(studyParticipantRepository.findParticipantByStatus(study.getId(), applier.getId(), APPROVE)).isPresent(),
                () -> assertThat(studyParticipantRepository.findParticipantByStatus(study.getId(), applier.getId(), GRADUATED)).isEmpty()
        );

        studyParticipantRepository.updateParticipantStatus(study.getId(), applier.getId(), GRADUATED);
        assertAll(
                () -> assertThat(studyParticipantRepository.findParticipantByStatus(study.getId(), applier.getId(), APPLY)).isEmpty(),
                () -> assertThat(studyParticipantRepository.findParticipantByStatus(study.getId(), applier.getId(), APPROVE)).isEmpty(),
                () -> assertThat(studyParticipantRepository.findParticipantByStatus(study.getId(), applier.getId(), GRADUATED)).isPresent()
        );
    }

    @Test
    @DisplayName("스터디 참여자들의 ID(PK)를 ParticipantStatus에 따라 조회한다")
    void findStudyParticipantIds() {
        studyParticipantRepository.save(StudyParticipant.applyInStudy(study.getId(), applier.getId()));

        final List<Long> applyParticipantIds1 = studyParticipantRepository.findParticipantIdsByStatus(study.getId(), APPLY);
        final List<Long> approveParticipantIds1 = studyParticipantRepository.findParticipantIdsByStatus(study.getId(), APPROVE);
        assertAll(
                () -> assertThat(applyParticipantIds1).hasSize(1),
                () -> assertThat(applyParticipantIds1).containsExactlyInAnyOrder(applier.getId()),
                () -> assertThat(approveParticipantIds1).hasSize(1),
                () -> assertThat(approveParticipantIds1).containsExactlyInAnyOrder(host.getId())
        );

        /* applier participant */
        studyParticipantRepository.updateParticipantStatus(study.getId(), applier.getId(), APPROVE);

        final List<Long> applyParticipantIds2 = studyParticipantRepository.findParticipantIdsByStatus(study.getId(), APPLY);
        final List<Long> approveParticipantIds2 = studyParticipantRepository.findParticipantIdsByStatus(study.getId(), APPROVE);
        assertAll(
                () -> assertThat(applyParticipantIds2).isEmpty(),
                () -> assertThat(approveParticipantIds2).hasSize(2),
                () -> assertThat(approveParticipantIds2).containsExactlyInAnyOrder(host.getId(), applier.getId())
        );
    }

    @Test
    @DisplayName("스터디 참여자 상태를 업데이트한다")
    void updateParticipantStatus() {
        // given
        final StudyParticipant participant = studyParticipantRepository.save(StudyParticipant.applyInStudy(study.getId(), applier.getId()));

        final StudyParticipant findParticipant1 = studyParticipantRepository.findById(participant.getId()).orElseThrow();
        assertThat(findParticipant1.getStatus()).isEqualTo(APPLY);

        /* applier -> APPROVE */
        studyParticipantRepository.updateParticipantStatus(study.getId(), applier.getId(), APPROVE);

        final StudyParticipant findParticipant2 = studyParticipantRepository.findById(participant.getId()).orElseThrow();
        assertThat(findParticipant2.getStatus()).isEqualTo(APPROVE);

        /* applier -> GRADUATED */
        studyParticipantRepository.updateParticipantStatus(study.getId(), applier.getId(), GRADUATED);

        final StudyParticipant findParticipant3 = studyParticipantRepository.findById(participant.getId()).orElseThrow();
        assertThat(findParticipant3.getStatus()).isEqualTo(GRADUATED);
    }
}
