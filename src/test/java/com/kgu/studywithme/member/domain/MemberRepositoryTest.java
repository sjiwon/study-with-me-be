package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.common.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.kgu.studywithme.fixture.MemberFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> MemberRepository 테스트")
class MemberRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    private Member member;
    private final Member[] participants = new Member[5];

    @BeforeEach
    void setUp() {
        member = memberRepository.save(JIWON.toMember());
        participants[0] = memberRepository.save(GHOST.toMember());
        participants[1] = memberRepository.save(DUMMY1.toMember());
        participants[2] = memberRepository.save(DUMMY2.toMember());
        participants[3] = memberRepository.save(DUMMY3.toMember());
        participants[4] = memberRepository.save(DUMMY4.toMember());
    }

    @Test
    @DisplayName("이메일로 사용자를 조회한다")
    void findByEmail() {
        // given
        final Email same = member.getEmail();
        final Email diff = Email.from("diff" + member.getEmailValue());

        // when
        Optional<Member> findMember1 = memberRepository.findByEmail(same);
        Optional<Member> findMember2 = memberRepository.findByEmail(diff);

        // then
        assertAll(
                () -> assertThat(findMember1).isPresent(),
                () -> assertThat(findMember1.get()).isEqualTo(member),
                () -> assertThat(findMember2).isEmpty()
        );
    }

    @Test
    @DisplayName("결석한 참여자들의 Score를 일괄 업데이트한다 [For Scheduling]")
    void applyAbsenceScore() {
        // given
        final Set<Long> absenceParticipantIds = Set.of(
                participants[2].getId(),
                participants[3].getId()
        );

        // when
        memberRepository.applyAbsenceScore(absenceParticipantIds);

        // then
        List<Integer> expectScores = List.of(
                80,
                80,
                80,
                80 - 5,
                80 - 5,
                80
        );
        List<Member> members = memberRepository.findAll();

        for (int i = 0; i < expectScores.size(); i++) {
            Member member = members.get(i);
            int expectScore = expectScores.get(i);

            assertThat(member.getScore()).isEqualTo(expectScore);
        }
    }
}
