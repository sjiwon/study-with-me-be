package com.kgu.studywithme.study.infrastructure.repository.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study -> StudyDuplicateCheckRepository 테스트")
class StudyDuplicateCheckRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Study studyA;
    private Study studyB;

    @BeforeEach
    void setUp() {
        final Member host = memberRepository.save(JIWON.toMember());
        studyA = studyRepository.save(SPRING.toOnlineStudy(host.getId()));
        studyB = studyRepository.save(JPA.toOnlineStudy(host.getId()));
    }

    @Test
    @DisplayName("이름에 해당하는 스터디가 존재하는지 확인한다")
    void isNameExists() {
        // when
        final boolean actual1 = studyRepository.isNameExists(studyA.getNameValue());
        final boolean actual2 = studyRepository.isNameExists("diff" + studyA.getNameValue());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("다른 스터디가 해당 이름을 사용하고 있는지 확인한다")
    void isNameUsedByOther() {
        // when
        final boolean actual1 = studyRepository.isNameUsedByOther(studyA.getId(), studyB.getNameValue());
        final boolean actual2 = studyRepository.isNameUsedByOther(studyB.getId(), studyB.getNameValue());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
