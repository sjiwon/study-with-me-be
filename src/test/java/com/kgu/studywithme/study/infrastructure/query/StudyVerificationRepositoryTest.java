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

import static com.kgu.studywithme.common.fixture.MemberFixture.ANONYMOUS;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(StudyVerificationRepository.class)
@DisplayName("Study -> StudyVerificationRepository 테스트")
class StudyVerificationRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyVerificationRepository studyVerificationRepository;

    @Autowired
    private StudyJpaRepository studyJpaRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    private Member host;
    private Member anonymous;
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberJpaRepository.save(JIWON.toMember());
        anonymous = memberJpaRepository.save(ANONYMOUS.toMember());
        study = studyJpaRepository.save(SPRING.toOnlineStudy(host.getId()));
    }

    @Test
    @DisplayName("스터디 팀장인지 확인한다")
    void isHost() {
        assertAll(
                () -> assertThat(studyVerificationRepository.isHost(study.getId(), host.getId())).isTrue(),
                () -> assertThat(studyVerificationRepository.isHost(study.getId(), anonymous.getId())).isFalse()
        );
    }
}