package com.kgu.studywithme.member.infrastructure.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.infrastructure.persistence.MemberJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(MemberDuplicateCheckRepository.class)
@DisplayName("Member -> MemberDuplicateCheckRepository 테스트")
class MemberDuplicateCheckRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberDuplicateCheckRepository memberDuplicateCheckRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    private Member memberA;
    private Member memberB;

    @BeforeEach
    void setUp() {
        memberA = memberJpaRepository.save(JIWON.toMember());
        memberB = memberJpaRepository.save(GHOST.toMember());
    }

    @Test
    @DisplayName("이메일에 해당하는 사용자가 존재하는지 확인한다")
    void isEmailExists() {
        // when
        final boolean actual1 = memberDuplicateCheckRepository.isEmailExists(memberA.getEmail().getValue());
        final boolean actual2 = memberDuplicateCheckRepository.isEmailExists("diff" + memberA.getEmail().getValue());

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
        final boolean actual1 = memberDuplicateCheckRepository.isNicknameExists(memberA.getNickname().getValue());
        final boolean actual2 = memberDuplicateCheckRepository.isNicknameExists("diff" + memberA.getNickname().getValue());

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
        final boolean actual1 = memberDuplicateCheckRepository.isNicknameUsedByOther(memberA.getId(), memberB.getNickname().getValue());
        final boolean actual2 = memberDuplicateCheckRepository.isNicknameUsedByOther(memberB.getId(), memberB.getNickname().getValue());

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
        final boolean actual1 = memberDuplicateCheckRepository.isPhoneExists(memberA.getPhone().getValue());
        final boolean actual2 = memberDuplicateCheckRepository.isPhoneExists(memberA.getPhone().getValue().replaceAll("0", "1"));

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
        final boolean actual1 = memberDuplicateCheckRepository.isPhoneUsedByOther(memberA.getId(), memberB.getPhone().getValue());
        final boolean actual2 = memberDuplicateCheckRepository.isPhoneUsedByOther(memberB.getId(), memberB.getPhone().getValue());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
