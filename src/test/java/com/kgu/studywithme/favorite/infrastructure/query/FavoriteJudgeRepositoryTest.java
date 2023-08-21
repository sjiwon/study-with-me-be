package com.kgu.studywithme.favorite.infrastructure.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.favorite.infrastructure.persistence.FavoriteJpaRepository;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.infrastructure.persistence.MemberJpaRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.infrastructure.persistence.StudyJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(FavoriteJudgeRepository.class)
@DisplayName("Favorite -> FavoriteJudgeRepository 테스트")
public class FavoriteJudgeRepositoryTest extends RepositoryTest {
    @Autowired
    private FavoriteJudgeRepository favoriteJudgeRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private StudyJpaRepository studyJpaRepository;

    @Autowired
    private FavoriteJpaRepository favoriteJpaRepository;

    private Member member;
    private Study studyA;
    private Study studyB;

    @BeforeEach
    void setUp() {
        member = memberJpaRepository.save(JIWON.toMember());
        studyA = studyJpaRepository.save(SPRING.toOnlineStudy(member.getId()));
        studyB = studyJpaRepository.save(JPA.toOnlineStudy(member.getId()));
    }

    @Test
    @DisplayName("해당 스터디를 이미 찜했는지 확인한다")
    void alreadyLikeMarked() {
        // given
        favoriteJpaRepository.save(Favorite.favoriteMarking(member.getId(), studyA.getId()));

        // when
        final boolean actual1 = favoriteJudgeRepository.alreadyLikeMarked(member.getId(), studyA.getId());
        final boolean actual2 = favoriteJudgeRepository.alreadyLikeMarked(member.getId(), studyB.getId());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("해당 스터디를 찜한 적 없는지 확인한다")
    void neverLikeMarked() {
        // given
        favoriteJpaRepository.save(Favorite.favoriteMarking(member.getId(), studyA.getId()));

        // when
        final boolean actual1 = favoriteJudgeRepository.neverLikeMarked(member.getId(), studyA.getId());
        final boolean actual2 = favoriteJudgeRepository.neverLikeMarked(member.getId(), studyB.getId());

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue()
        );
    }
}
