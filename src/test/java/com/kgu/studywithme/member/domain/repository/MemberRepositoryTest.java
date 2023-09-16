package com.kgu.studywithme.member.domain.repository;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.model.Score;
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
    private MemberRepository sut;

    @Test
    @DisplayName("결석자들의 Score를 감소시킨다 [결석 패널티 -5점]")
    void applyScoreToAbsenceParticipant() {
        // given
        final Member memberA = sut.save(JIWON.toMember());
        final int scoreOfMemberA = memberA.getScore().getValue();

        final Member memberB = sut.save(GHOST.toMember());
        final int scoreOfMemberB = memberB.getScore().getValue();

        final Member memberC = sut.save(ANONYMOUS.toMember());
        final int scoreOfMemberC = memberC.getScore().getValue();

        // when
        sut.applyScoreToAbsenceParticipant(Set.of(memberB.getId(), memberC.getId()));

        // then
        final List<Member> members = sut.findAll();
        assertThat(members)
                .map(Member::getScore)
                .map(Score::getValue)
                .containsExactly(
                        scoreOfMemberA,
                        scoreOfMemberB - 5,
                        scoreOfMemberC - 5
                );
    }

    @Test
    @DisplayName("이메일로 사용자를 조회한다")
    void findByEmail() {
        // given
        final Member member = sut.save(JIWON.toMember());
        final String email = member.getEmail().getValue();

        // when
        final Member findMember = sut.findByEmail(email).orElseThrow();
        final Optional<Member> emptyMember = sut.findByEmail("diff" + email);

        // then
        assertAll(
                () -> assertThat(findMember).isEqualTo(member),
                () -> assertThat(emptyMember).isEmpty()
        );
    }

    @Test
    @DisplayName("해당 닉네임을 사용하고 있는 Member Ids를 조회한다")
    void findIdByNicknameUsed() {
        // given
        final Member memberA = sut.save(JIWON.toMember());
        final Member memberB = sut.save(GHOST.toMember());
        final Member memberC = sut.save(ANONYMOUS.toMember());

        // when
        final List<Long> ids1 = sut.findIdByNicknameUsed(memberA.getNickname().getValue());
        final List<Long> ids2 = sut.findIdByNicknameUsed(memberB.getNickname().getValue());
        final List<Long> ids3 = sut.findIdByNicknameUsed(memberC.getNickname().getValue());

        // then
        assertAll(
                () -> assertThat(ids1).containsExactlyInAnyOrder(memberA.getId()),
                () -> assertThat(ids2).containsExactlyInAnyOrder(memberB.getId()),
                () -> assertThat(ids3).containsExactlyInAnyOrder(memberC.getId())
        );
    }

    @Test
    @DisplayName("해당 전화번호를 사용하고 있는 Member Ids를 조회한다")
    void findIdByPhoneUsed() {
        // given
        final Member memberA = sut.save(JIWON.toMember());
        final Member memberB = sut.save(GHOST.toMember());
        final Member memberC = sut.save(ANONYMOUS.toMember());

        // when
        final List<Long> ids1 = sut.findIdByPhoneUsed(memberA.getPhone().getValue());
        final List<Long> ids2 = sut.findIdByPhoneUsed(memberB.getPhone().getValue());
        final List<Long> ids3 = sut.findIdByPhoneUsed(memberC.getPhone().getValue());

        // then
        assertAll(
                () -> assertThat(ids1).containsExactlyInAnyOrder(memberA.getId()),
                () -> assertThat(ids2).containsExactlyInAnyOrder(memberB.getId()),
                () -> assertThat(ids3).containsExactlyInAnyOrder(memberC.getId())
        );
    }

    @Test
    @DisplayName("이메일이 사용중인지 확인한다")
    void existsByEmailValue() {
        // given
        final Member member = sut.save(JIWON.toMember());
        final String email = member.getEmail().getValue();

        // when
        final boolean actual1 = sut.existsByEmailValue(email);
        final boolean actual2 = sut.existsByEmailValue("diff" + email);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("닉네임이 사용중인지 확인한다")
    void existsByNicknameValue() {
        // given
        final Member member = sut.save(JIWON.toMember());
        final String nickname = member.getNickname().getValue();

        // when
        final boolean actual1 = sut.existsByNicknameValue(nickname);
        final boolean actual2 = sut.existsByNicknameValue("diff" + nickname);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("전화번호가 사용중인지 확인한다")
    void existsByPhoneValue() {
        // given
        final Member member = sut.save(JIWON.toMember());
        final String phone = member.getPhone().getValue();

        // when
        final boolean actual1 = sut.existsByPhoneValue(phone);
        final boolean actual2 = sut.existsByPhoneValue("diff" + phone);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
