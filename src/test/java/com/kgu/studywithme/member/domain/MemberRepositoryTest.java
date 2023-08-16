package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.common.RepositoryTest;
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

@DisplayName("Member -> MemberRepository 테스트")
class MemberRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    private Member memberA;
    private Member memberB;
    private Member memberC;

    @BeforeEach
    void setUp() {
        memberA = memberRepository.save(JIWON.toMember());
        memberB = memberRepository.save(GHOST.toMember());
        memberC = memberRepository.save(ANONYMOUS.toMember());
    }

    @Test
    @DisplayName("이메일로 사용자를 조회한다")
    void findByEmail() {
        // when
        final Member findMember = memberRepository.findByEmail(memberA.getEmail().getValue()).orElseThrow();
        final Optional<Member> emptyMember = memberRepository.findByEmail("diff" + memberA.getEmail().getValue());

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
        memberRepository.applyScoreToAbsenceParticipant(Set.of(memberB.getId(), memberC.getId()));

        // then
        final List<Member> members = memberRepository.findAll();
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
