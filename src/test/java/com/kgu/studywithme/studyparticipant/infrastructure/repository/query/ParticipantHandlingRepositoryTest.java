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

@DisplayName("StudyParticipant -> ParticipantHandlingRepository 테스트")
class ParticipantHandlingRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyParticipantRepository studyParticipantRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    private Member host;
    private Member applier;
    private Member participant;
    private Member participateCancelMember;
    private Member graduatedMember;
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        applier = memberRepository.save(DUMMY1.toMember());
        participant = memberRepository.save(DUMMY2.toMember());
        participateCancelMember = memberRepository.save(DUMMY3.toMember());
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
                StudyParticipant.applyParticipant(study.getId(), participateCancelMember.getId(), CALCEL)
        );
        studyParticipantRepository.save(
                StudyParticipant.applyParticipant(study.getId(), graduatedMember.getId(), GRADUATED)
        );
    }

    @Test
    @DisplayName("스터디 신청 취소에 의해 신청자 정보를 삭제한다")
    void deleteApplier() {
        assertAll(
                () -> assertThat(
                        studyParticipantRepository.deleteApplier(study.getId(), host.getId())
                ).isEqualTo(0),
                () -> assertThat(
                        studyParticipantRepository.deleteApplier(study.getId(), applier.getId())
                ).isEqualTo(1),
                () -> assertThat(
                        studyParticipantRepository.deleteApplier(study.getId(), participant.getId())
                ).isEqualTo(0),
                () -> assertThat(
                        studyParticipantRepository.deleteApplier(study.getId(), participateCancelMember.getId())
                ).isEqualTo(0),
                () -> assertThat(
                        studyParticipantRepository.deleteApplier(study.getId(), graduatedMember.getId())
                ).isEqualTo(0)
        );
    }

    @Test
    @DisplayName("스터디 신청자를 조회한다 [With studyId + memberId]")
    void findApplier() {
        assertAll(
                () -> assertThat(
                        studyParticipantRepository.findApplier(study.getId(), host.getId())
                ).isEmpty(),
                () -> assertThat(
                        studyParticipantRepository.findApplier(study.getId(), applier.getId())
                ).isPresent(),
                () -> assertThat(
                        studyParticipantRepository.findApplier(study.getId(), participant.getId())
                ).isEmpty(),
                () -> assertThat(
                        studyParticipantRepository.findApplier(study.getId(), participateCancelMember.getId())
                ).isEmpty(),
                () -> assertThat(
                        studyParticipantRepository.findApplier(study.getId(), graduatedMember.getId())
                ).isEmpty()
        );
    }

    @Test
    @DisplayName("스터디 참여자를 조회한다 [With studyId + memberId]")
    void findParticipant() {
        assertAll(
                () -> assertThat(
                        studyParticipantRepository.findParticipant(study.getId(), host.getId())
                ).isPresent(),
                () -> assertThat(
                        studyParticipantRepository.findParticipant(study.getId(), applier.getId())
                ).isEmpty(),
                () -> assertThat(
                        studyParticipantRepository.findParticipant(study.getId(), participant.getId())
                ).isPresent(),
                () -> assertThat(
                        studyParticipantRepository.findParticipant(study.getId(), participateCancelMember.getId())
                ).isEmpty(),
                () -> assertThat(
                        studyParticipantRepository.findParticipant(study.getId(), graduatedMember.getId())
                ).isEmpty()
        );
    }
}
