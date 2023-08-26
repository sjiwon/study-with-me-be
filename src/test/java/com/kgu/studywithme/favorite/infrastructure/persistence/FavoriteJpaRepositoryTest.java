package com.kgu.studywithme.favorite.infrastructure.persistence;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.infrastructure.persistence.MemberJpaRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.infrastructure.persistence.StudyJpaRepository;
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
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private StudyJpaRepository studyJpaRepository;

    private Member member;
    private Study study;

    @BeforeEach
    void setUp() {
        member = memberJpaRepository.save(JIWON.toMember());
        study = studyJpaRepository.save(SPRING.toOnlineStudy(member.getId()));
    }

    @Test
    @DisplayName("특정 스터디에 대한 사용자 찜 현황을 삭제한다")
    void cancelLikeMarking() {
        // given
        final Favorite favorite = favoriteJpaRepository.save(Favorite.favoriteMarking(member.getId(), study.getId()));
        assertThat(favoriteJpaRepository.existsById(favorite.getId())).isTrue();

        // when
        favoriteJpaRepository.cancelLikeMarking(member.getId(), study.getId());

        // then
        assertThat(favoriteJpaRepository.existsById(favorite.getId())).isFalse();
    }
}
