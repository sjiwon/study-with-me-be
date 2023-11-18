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

import java.util.Optional;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Favorite -> FavoriteRepository 테스트")
class FavoriteRepositoryTest extends RepositoryTest {
    @Autowired
    private FavoriteRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    private Member host;
    private Member member;
    private Study studyA;
    private Study studyB;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        member = memberRepository.save(GHOST.toMember());
        studyA = studyRepository.save(SPRING.toStudy(host.getId()));
        studyB = studyRepository.save(JPA.toStudy(host.getId()));
    }

    @Test
    @DisplayName("특정 스터디에 대해서 사용자가 찜한 기록한 기록을 조회한다")
    void findByStudyIdAndMemberId() {
        // given
        sut.save(Favorite.favoriteMarking(host, studyA));
        sut.save(Favorite.favoriteMarking(member, studyB));

        // when
        final Optional<Favorite> actual1 = sut.findByStudyIdAndMemberId(studyA.getId(), host.getId());
        final Optional<Favorite> actual2 = sut.findByStudyIdAndMemberId(studyA.getId(), member.getId());
        final Optional<Favorite> actual3 = sut.findByStudyIdAndMemberId(studyB.getId(), host.getId());
        final Optional<Favorite> actual4 = sut.findByStudyIdAndMemberId(studyB.getId(), member.getId());

        // then
        assertAll(
                () -> assertThat(actual1).isPresent(),
                () -> assertThat(actual2).isEmpty(),
                () -> assertThat(actual3).isEmpty(),
                () -> assertThat(actual4).isPresent()
        );
    }
}
