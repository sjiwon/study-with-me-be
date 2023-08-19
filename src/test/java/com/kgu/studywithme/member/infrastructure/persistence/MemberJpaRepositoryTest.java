package com.kgu.studywithme.member.infrastructure.persistence;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.kgu.studywithme.common.fixture.MemberFixture.ANONYMOUS;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> MemberJpaRepository 테스트")
class MemberJpaRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberJpaRepository memberJpaRepository;

    private Member memberA;
    private Member memberB;
    private Member memberC;

    @BeforeEach
    void setUp() {
        memberA = memberJpaRepository.save(JIWON.toMember());
        memberB = memberJpaRepository.save(GHOST.toMember());
        memberC = memberJpaRepository.save(ANONYMOUS.toMember());
    }

    @Test
    @DisplayName("이메일로 사용자를 조회한다")
    void findByEmail() {
        // when
        final Member findMember = memberJpaRepository.findByEmail(memberA.getEmail().getValue()).orElseThrow();
        final Optional<Member> emptyMember = memberJpaRepository.findByEmail("diff" + memberA.getEmail().getValue());

        // then
        assertAll(
                () -> assertThat(findMember).isEqualTo(memberA),
                () -> assertThat(emptyMember).isEmpty()
        );
    }

    @Test
    @DisplayName("결석자들의 Score를 감소시킨다 [결석 패널티 -5점]")
    void applyScoreToAbsenceParticipant() {
        // given
        final int scoreOfMemberA = memberA.getScore().getValue();
        final int scoreOfMemberB = memberB.getScore().getValue();
        final int scoreOfMemberC = memberC.getScore().getValue();

        // when
        memberJpaRepository.applyScoreToAbsenceParticipant(Set.of(memberB.getId(), memberC.getId()));

        // then
        final List<Member> members = memberJpaRepository.findAll();
        assertThat(members)
                .map(Member::getScore)
                .map(Score::getValue)
                .containsExactly(
                        scoreOfMemberA,
                        scoreOfMemberB - 5,
                        scoreOfMemberC - 5
                );
    }
}
