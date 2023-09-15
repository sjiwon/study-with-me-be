package com.kgu.studywithme.auth.infrastructure.persistence;

import com.kgu.studywithme.auth.domain.model.Token;
import com.kgu.studywithme.auth.domain.repository.TokenRepository;
import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(RdbTokenPersistenceAdapter.class)
@DisplayName("Auth -> RdbTokenPersistenceAdapter 테스트")
class RdbTokenPersistenceAdapterTest extends RepositoryTest {
    @Autowired
    private RdbTokenPersistenceAdapter rdbTokenPersistenceAdapter;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(JIWON.toMember());
    }

    @Nested
    @DisplayName("RefreshToken 동기화")
    class SynchronizedRefreshToken {
        @Test
        @DisplayName("RefreshToken을 보유하고 있지 않은 사용자는 새로운 RefreshToken을 발급한다")
        void reissueRefreshToken() {
            // when
            rdbTokenPersistenceAdapter.synchronizeRefreshToken(member.getId(), REFRESH_TOKEN);

            // then
            final Token findToken = tokenRepository.findByMemberId(member.getId()).orElseThrow();
            assertThat(findToken.getRefreshToken()).isEqualTo(REFRESH_TOKEN);
        }

        @Test
        @DisplayName("RefreshToken을 보유하고 있는 사용자는 새로운 RefreshToken으로 업데이트한다")
        void updateRefreshToken() {
            // given
            tokenRepository.save(Token.issueRefreshToken(member.getId(), REFRESH_TOKEN));

            // when
            final String newRefreshToken = REFRESH_TOKEN + "new";
            rdbTokenPersistenceAdapter.synchronizeRefreshToken(member.getId(), newRefreshToken);

            // then
            final Token findToken = tokenRepository.findByMemberId(member.getId()).orElseThrow();
            assertThat(findToken.getRefreshToken()).isEqualTo(newRefreshToken);
        }
    }

    @Test
    @DisplayName("사용자의 RefreshToken을 재발급한다")
    void updateMemberRefreshToken() {
        // given
        tokenRepository.save(Token.issueRefreshToken(member.getId(), REFRESH_TOKEN));

        // when
        final String newRefreshToken = REFRESH_TOKEN + "new";
        rdbTokenPersistenceAdapter.updateMemberRefreshToken(member.getId(), newRefreshToken);

        // then
        final Token findToken = tokenRepository.findByMemberId(member.getId()).orElseThrow();
        assertThat(findToken.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken을 삭제한다")
    void deleteMemberRefreshToken() {
        // given
        tokenRepository.save(Token.issueRefreshToken(member.getId(), REFRESH_TOKEN));

        // when
        rdbTokenPersistenceAdapter.deleteMemberRefreshToken(member.getId());

        // then
        assertThat(tokenRepository.findByMemberId(member.getId())).isEmpty();
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken인지 확인한다")
    void isMemberRefreshToken() {
        // given
        tokenRepository.save(Token.issueRefreshToken(member.getId(), REFRESH_TOKEN));

        // when
        final boolean actual1 = rdbTokenPersistenceAdapter.isMemberRefreshToken(member.getId(), REFRESH_TOKEN);
        final boolean actual2 = rdbTokenPersistenceAdapter.isMemberRefreshToken(member.getId(), REFRESH_TOKEN + "fake");

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
