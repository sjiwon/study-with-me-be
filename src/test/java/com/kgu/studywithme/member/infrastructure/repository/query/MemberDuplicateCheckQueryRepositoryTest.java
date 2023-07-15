package com.kgu.studywithme.member.infrastructure.repository.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> MemberDuplicateCheckQueryRepository 테스트")
class MemberDuplicateCheckQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    private Member memberA;
    private Member memberB;

    @BeforeEach
    void setUp() {
        memberA = memberRepository.save(JIWON.toMember());
        memberB = memberRepository.save(GHOST.toMember());
    }

    @Test
    @DisplayName("이메일에 해당하는 사용자가 존재하는지 확인한다")
    void isEmailExists() {
        // when
        boolean actual1 = memberRepository.isEmailExists(memberA.getEmailValue());
        boolean actual2 = memberRepository.isEmailExists("diff" + memberA.getEmailValue());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("닉네임에 해당하는 사용자가 존재하는지 확인한다")
    void isNicknameExists() {
        // when
        boolean actual1 = memberRepository.isNicknameExists(memberA.getNicknameValue());
        boolean actual2 = memberRepository.isNicknameExists("diff" + memberA.getNicknameValue());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("다른 사람이 해당 닉네임을 사용하고 있는지 확인한다")
    void isNicknameUsedByOther() {
        // when
        boolean actual1 = memberRepository.isNicknameUsedByOther(memberA.getId(), memberB.getNicknameValue());
        boolean actual2 = memberRepository.isNicknameUsedByOther(memberB.getId(), memberB.getNicknameValue());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("전화번호에 해당하는 사용자가 존재하는지 확인한다")
    void isPhoneExists() {
        // when
        boolean actual1 = memberRepository.isPhoneExists(memberA.getPhone());
        boolean actual2 = memberRepository.isPhoneExists(memberA.getPhone().replaceAll("0", "1"));

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("다른 사람이 해당 전화번호를 사용하고 있는지 확인한다")
    void isPhoneUsedByOther() {
        // when
        boolean actual1 = memberRepository.isPhoneUsedByOther(memberA.getId(), memberB.getPhone());
        boolean actual2 = memberRepository.isPhoneUsedByOther(memberB.getId(), memberB.getPhone());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
