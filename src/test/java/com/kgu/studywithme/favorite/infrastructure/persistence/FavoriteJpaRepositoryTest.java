package com.kgu.studywithme.favorite.infrastructure.persistence;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Favorite -> FavoriteJpaRepository 테스트")
class FavoriteJpaRepositoryTest extends RepositoryTest {
    @Autowired
    private FavoriteJpaRepository favoriteJpaRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    private Member member;
    private Study study;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(JIWON.toMember());
        study = studyRepository.save(SPRING.toOnlineStudy(member.getId()));
    }

    @Test
    @DisplayName("특정 스터디에 대한 사용자 찜 현황을 삭제한다")
    void cancelLikeMarking() {
        // given
        final Favorite favorite = favoriteJpaRepository.save(Favorite.favoriteMarking(study.getId(), member.getId()));
        assertThat(favoriteJpaRepository.existsById(favorite.getId())).isTrue();

        // when
        favoriteJpaRepository.cancelLikeMarking(study.getId(), member.getId());

        // then
        assertThat(favoriteJpaRepository.existsById(favorite.getId())).isFalse();
    }
}
