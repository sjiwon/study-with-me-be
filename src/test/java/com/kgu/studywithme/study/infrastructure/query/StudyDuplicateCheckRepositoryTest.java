package com.kgu.studywithme.study.infrastructure.query;

import com.kgu.studywithme.common.RepositoryTest;
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

@Import(StudyDuplicateCheckRepository.class)
@DisplayName("Study -> StudyDuplicateCheckRepository 테스트")
class StudyDuplicateCheckRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyDuplicateCheckRepository studyDuplicateCheckRepository;

    @Autowired
    private StudyJpaRepository studyJpaRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    private Study studyA;
    private Study studyB;

    @BeforeEach
    void setUp() {
        final Member host = memberJpaRepository.save(JIWON.toMember());
        studyA = studyJpaRepository.save(SPRING.toOnlineStudy(host.getId()));
        studyB = studyJpaRepository.save(JPA.toOnlineStudy(host.getId()));
    }

    @Test
    @DisplayName("이름에 해당하는 스터디가 존재하는지 확인한다")
    void isNameExists() {
        // when
        final boolean actual1 = studyDuplicateCheckRepository.isNameExists(studyA.getName().getValue());
        final boolean actual2 = studyDuplicateCheckRepository.isNameExists("diff" + studyA.getName().getValue());

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
        final boolean actual1 = studyDuplicateCheckRepository.isNameUsedByOther(studyA.getId(), studyB.getName().getValue());
        final boolean actual2 = studyDuplicateCheckRepository.isNameUsedByOther(studyB.getId(), studyB.getName().getValue());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
