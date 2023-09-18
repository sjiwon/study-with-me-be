package com.kgu.studywithme.study.domain.repository;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    @DisplayName("스터디 팀장인지 확인한다")
    void isHost() {
        // given
        final Study study = sut.save(SPRING.toOnlineStudy(host.getId()));

        // when
        final boolean actual1 = sut.isHost(study.getId(), host.getId());
        final boolean actual2 = sut.isHost(study.getId(), host.getId() + 1L);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
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

    @Test
    @DisplayName("현재 모집중인 스터디를 조회한다 (studyId에 해당하는 Study가 현재 모집중인지)")
    void getRecruitingStudy() {
        /* 모집중 */
        final Study study = sut.save(SPRING.toOnlineStudy(host.getId()));
        assertThat(sut.getRecruitingStudy(study.getId())).isEqualTo(study);

        /* 모집 마감 */
        study.recruitmentOff();
        assertThatThrownBy(() -> sut.getRecruitingStudy(study.getId()))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.STUDY_IS_NOT_RECRUITING_NOW.getMessage());
    }

    @Test
    @DisplayName("현재 진행중인 스터디를 조회한다 (studyId에 해당하는 Study가 현재 진행중인지)")
    void getInProgressStudy() {
        /* 진행중 */
        final Study study = sut.save(SPRING.toOnlineStudy(host.getId()));
        assertThat(sut.getInProgressStudy(study.getId())).isEqualTo(study);

        /* 종료 */
        study.terminate();
        assertThatThrownBy(() -> sut.getInProgressStudy(study.getId()))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.STUDY_IS_TERMINATED.getMessage());
    }
}
