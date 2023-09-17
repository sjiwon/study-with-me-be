package com.kgu.studywithme.study.domain.repository;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study -> StudyRepository 테스트")
public class StudyRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    private Member host;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
    }

    @Test
    @DisplayName("해당 이름을 사용하고 있는 StudyId를 조회한다")
    void findIdByNameUsed() {
        // given
        final Study studyA = sut.save(SPRING.toOnlineStudy(host.getId()));
        final Study studyB = sut.save(JPA.toOnlineStudy(host.getId()));

        // when
        final Long ids1 = sut.findIdByNameUsed(studyA.getName().getValue());
        final Long ids2 = sut.findIdByNameUsed(studyB.getName().getValue());
        final Long ids3 = sut.findIdByNameUsed(studyB.getName().getValue() + "diff");

        // then
        assertAll(
                () -> assertThat(ids1).isEqualTo(studyA.getId()),
                () -> assertThat(ids2).isEqualTo(studyB.getId()),
                () -> assertThat(ids3).isNull()
        );
    }
    
    @Test
    @DisplayName("스터디의 HostId를 조회한다")
    void getHostId() {
        // given
        final Study studyA = sut.save(SPRING.toOnlineStudy(host.getId()));
        final Study studyB = sut.save(JPA.toOnlineStudy(host.getId()));

        // when
        final Long hostIdA = sut.getHostId(studyA.getId());
        final Long hostIdB = sut.getHostId(studyB.getId());

        // then
        assertAll(
                () -> assertThat(hostIdA).isEqualTo(host.getId()),
                () -> assertThat(hostIdB).isEqualTo(host.getId())
        );
    }

    @Test
    @DisplayName("이름이 사용중인지 확인한다")
    void existsByNameValue() {
        // given
        final Study study = sut.save(SPRING.toOnlineStudy(host.getId()));
        final String name = study.getName().getValue();

        // when
        final boolean actual1 = sut.existsByNameValue(name);
        final boolean actual2 = sut.existsByNameValue("diff" + name);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
