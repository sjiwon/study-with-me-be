package com.kgu.studywithme.favorite.domain.repository;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.favorite.domain.model.Favorite;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Favorite -> FavoriteRepository 테스트")
class FavoriteRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private FavoriteRepository sut;

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
        final Favorite favorite = sut.save(Favorite.favoriteMarking(member.getId(), study.getId()));
        assertThat(sut.existsById(favorite.getId())).isTrue();

        // when
        sut.cancelLikeMarking(member.getId(), study.getId());

        // then
        assertThat(sut.existsById(favorite.getId())).isFalse();
    }
}
