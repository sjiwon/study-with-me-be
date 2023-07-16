package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.common.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> MemberRepository 테스트")
class MemberRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(JIWON.toMember());
    }

    @Test
    @DisplayName("이메일로 사용자를 조회한다")
    void findByEmail() {
        // when
        Member findMember = memberRepository.findByEmail(member.getEmail()).orElseThrow();
        Optional<Member> emptyMember = memberRepository.findByEmail(Email.from("diff" + member.getEmailValue()));

        // then
        assertAll(
                () -> assertThat(findMember).isEqualTo(member),
                () -> assertThat(emptyMember).isEmpty()
        );
    }
}
