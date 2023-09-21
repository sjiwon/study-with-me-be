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
                        scoreOfMemberB + Score.ABSENCE,
                        scoreOfMemberC + Score.ABSENCE
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
    @DisplayName("해당 닉네임을 본인이 아닌 타인이 사용하고 있는지 확인한다")
    void isNicknameUsedByOther() {
        // given
        final Member memberA = sut.save(JIWON.toMember());
        final Member memberB = sut.save(GHOST.toMember());

        // when
        final boolean actual1 = sut.isNicknameUsedByOther(memberA.getId(), memberA.getNickname().getValue());
        final boolean actual2 = sut.isNicknameUsedByOther(memberA.getId(), memberB.getNickname().getValue());
        final boolean actual3 = sut.isNicknameUsedByOther(memberB.getId(), memberB.getNickname().getValue());
        final boolean actual4 = sut.isNicknameUsedByOther(memberB.getId(), memberA.getNickname().getValue());

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isFalse(),
                () -> assertThat(actual4).isTrue()
        );
    }

    @Test
    @DisplayName("해당 전화번호를 본인이 아닌 타인이 사용하고 있는지 확인한다")
    void isPhoneUsedByOther() {
        // given
        final Member memberA = sut.save(JIWON.toMember());
        final Member memberB = sut.save(GHOST.toMember());

        // when
        final boolean actual1 = sut.isPhoneUsedByOther(memberA.getId(), memberA.getPhone().getValue());
        final boolean actual2 = sut.isPhoneUsedByOther(memberA.getId(), memberB.getPhone().getValue());
        final boolean actual3 = sut.isPhoneUsedByOther(memberB.getId(), memberB.getPhone().getValue());
        final boolean actual4 = sut.isPhoneUsedByOther(memberB.getId(), memberA.getPhone().getValue());

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isFalse(),
                () -> assertThat(actual4).isTrue()
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
        final boolean actual2 = sut.existsByPhoneValue(phone.replaceAll("0", "9"));

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
