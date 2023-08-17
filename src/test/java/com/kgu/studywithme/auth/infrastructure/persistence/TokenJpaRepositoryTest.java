package com.kgu.studywithme.auth.infrastructure.persistence;

import com.kgu.studywithme.auth.domain.Token;
import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth -> TokenJpaRepository 테스트")
class TokenJpaRepositoryTest extends RepositoryTest {
    @Autowired
    private TokenJpaRepository tokenJpaRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(JIWON.toMember());
        tokenJpaRepository.save(Token.issueRefreshToken(member.getId(), REFRESH_TOKEN));
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken을 조회한다")
    void findByMemberId() {
        // when
        final Optional<Token> emptyToken = tokenJpaRepository.findByMemberId(member.getId() + 10000L);
        final Token findToken = tokenJpaRepository.findByMemberId(member.getId()).orElseThrow();

        // then
        assertAll(
                () -> assertThat(emptyToken).isEmpty(),
                () -> assertThat(findToken.getMemberId()).isEqualTo(member.getId()),
                () -> assertThat(findToken.getRefreshToken()).isEqualTo(REFRESH_TOKEN)
        );
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken을 재발급한다")
    void updateMemberRefreshToken() {
        // when
        final String newRefreshToken = REFRESH_TOKEN + "reissue";
        tokenJpaRepository.updateMemberRefreshToken(member.getId(), newRefreshToken);

        // then
        final Token findToken = tokenJpaRepository.findByMemberId(member.getId()).orElseThrow();
        assertThat(findToken.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken인지 확인한다")
    void existsByMemberIdAndRefreshToken() {
        // when
        final boolean actual1 = tokenJpaRepository.existsByMemberIdAndRefreshToken(member.getId(), REFRESH_TOKEN);
        final boolean actual2 = tokenJpaRepository.existsByMemberIdAndRefreshToken(member.getId(), "fake");

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken을 삭제한다")
    void deleteMemberRefreshToken() {
        // when
        tokenJpaRepository.deleteMemberRefreshToken(member.getId());

        // then
        assertThat(tokenJpaRepository.findByMemberId(member.getId())).isEmpty();
    }
}
