package com.kgu.studywithme.favorite.domain;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Favorite -> FavoriteRepository 테스트")
class FavoriteRepositoryTest extends RepositoryTest {
    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    private Member member;
    private Study studyA;
    private Study studyB;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(JIWON.toMember());
        studyA = studyRepository.save(SPRING.toOnlineStudy(member.getId()));
        studyB = studyRepository.save(JPA.toOnlineStudy(member.getId()));
    }

    @Test
    @DisplayName("특정 스터디에 대해서 사용자가 찜을 했는지 여부를 확인한다")
    void existsByStudyIdAndMemberId() {
        // given
        favoriteRepository.save(Favorite.favoriteMarking(studyA.getId(), member.getId()));

        // when
        final boolean actual1 = favoriteRepository.existsByStudyIdAndMemberId(studyA.getId(), member.getId());
        final boolean actual2 = favoriteRepository.existsByStudyIdAndMemberId(studyB.getId(), member.getId());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("특정 스터디에 대한 사용자 찜 현황을 삭제한다")
    void deleteByStudyIdAndMemberId() {
        // given
        favoriteRepository.save(Favorite.favoriteMarking(studyA.getId(), member.getId()));

        // when
        favoriteRepository.deleteByStudyIdAndMemberId(studyA.getId(), member.getId());

        // then
        assertThat(favoriteRepository.existsByStudyIdAndMemberId(studyA.getId(), member.getId())).isFalse();
    }
}
