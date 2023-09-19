package com.kgu.studywithme.auth.domain.repository;

import com.kgu.studywithme.auth.domain.model.Token;
import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth -> TokenRepository 테스트")
class TokenRepositoryTest extends RepositoryTest {
    @Autowired
    private TokenRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(JIWON.toMember());
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken을 조회한다")
    void findByMemberId() {
        // given
        sut.save(Token.issueRefreshToken(member.getId(), REFRESH_TOKEN));

        // when
        final Optional<Token> emptyToken = sut.findByMemberId(member.getId() + 10000L);
        final Token findToken = sut.findByMemberId(member.getId()).orElseThrow();

        // then
        assertAll(
                () -> assertThat(emptyToken).isEmpty(),
                () -> assertThat(findToken.getMemberId()).isEqualTo(member.getId()),
                () -> assertThat(findToken.getRefreshToken()).isEqualTo(REFRESH_TOKEN)
        );
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken을 재발급한다")
    void updateRefreshToken() {
        // given
        sut.save(Token.issueRefreshToken(member.getId(), REFRESH_TOKEN));

        // when
        final String newRefreshToken = REFRESH_TOKEN + "reissue";
        sut.updateRefreshToken(member.getId(), newRefreshToken);

        // then
        final Token findToken = sut.findByMemberId(member.getId()).orElseThrow();
        assertThat(findToken.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken인지 확인한다")
    void existsByMemberIdAndRefreshToken() {
        // given
        sut.save(Token.issueRefreshToken(member.getId(), REFRESH_TOKEN));

        // when
        final boolean actual1 = sut.existsByMemberIdAndRefreshToken(member.getId(), REFRESH_TOKEN);
        final boolean actual2 = sut.existsByMemberIdAndRefreshToken(member.getId(), "fake");

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken을 삭제한다")
    void deleteRefreshToken() {
        // given
        sut.save(Token.issueRefreshToken(member.getId(), REFRESH_TOKEN));
        assertThat(sut.findByMemberId(member.getId())).isPresent();

        // when
        sut.deleteRefreshToken(member.getId());

        // then
        assertThat(sut.findByMemberId(member.getId())).isEmpty();
    }
}
