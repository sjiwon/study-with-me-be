package com.kgu.studywithme.study.infrastructure.repository.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.domain.participant.Participant;
import com.kgu.studywithme.study.domain.participant.ParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyParticipant -> ParticipantVerificationRepository 테스트")
class ParticipantVerificationRepositoryTest extends RepositoryTest {
    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    private Member memberA;
    private Member memberB;
    private Study study;

    @BeforeEach
    void setUp() {
        memberA = memberRepository.save(JIWON.toMember());
        memberB = memberRepository.save(GHOST.toMember());
        study = studyRepository.save(SPRING.toOnlineStudy(memberA));

        participantRepository.save(Participant.applyInStudy(study, memberA));
        participantRepository.save(Participant.applyInStudy(study, memberB));
    }

    @Test
    @DisplayName("스터디 참여자인지 확인한다 (참여 상태 = APPROVE)")
    void isParticipant() {
        // TODO Participant Refactoring 후 완성
        
        /* APPROVE = memberA */
        assertAll(
                () -> assertThat(participantRepository.isParticipant(study.getId(), memberA.getId())).isTrue(),
                () -> assertThat(participantRepository.isParticipant(study.getId(), memberB.getId())).isFalse()
        );

        /* APPROVE = memberA + memberB */
        assertAll(
                () -> assertThat(participantRepository.isParticipant(study.getId(), memberA.getId())).isTrue(),
                () -> assertThat(participantRepository.isParticipant(study.getId(), memberB.getId())).isTrue()
        );
    }

    @Test
    @DisplayName("스터디 졸업자 확인한다 (참여 상태 = GRADUATED)")
    void isGraduatedParticipant() {
        // TODO Participant Refactoring 후 완성

        /* APPROVE = memberA */
        assertAll(
                () -> assertThat(participantRepository.isGraduatedParticipant(study.getId(), memberA.getId())).isTrue(),
                () -> assertThat(participantRepository.isGraduatedParticipant(study.getId(), memberB.getId())).isFalse()
        );

        /* APPROVE = memberA + memberB */
        assertAll(
                () -> assertThat(participantRepository.isGraduatedParticipant(study.getId(), memberA.getId())).isTrue(),
                () -> assertThat(participantRepository.isGraduatedParticipant(study.getId(), memberB.getId())).isTrue()
        );
    }
}
