package com.kgu.studywithme.studyparticipant.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPLY;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPROVE;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StudyParticipant -> 도메인 [StudyParticipant] 테스트")
class StudyParticipantTest extends ParallelTest {
    private final Member host = JIWON.toMember().apply(1L);
    private final Member member = GHOST.toMember().apply(2L);
    private final Study study = SPRING.toStudy(host).apply(1L);

    @Test
    @DisplayName("사용자가 스터디에 참여 신청을 한다")
    void applyInStudy() {
        // when
        final StudyParticipant participant = StudyParticipant.applyInStudy(study, member);

        // then
        assertThat(participant.getStatus()).isEqualTo(APPLY);
    }

    @Test
    @DisplayName("스터디를 생성하면 스터디 팀장은 자동으로 APPROVE상태로 참여한다")
    void applyHost() {
        // when
        final StudyParticipant participant = StudyParticipant.applyHost(study, host);

        // then
        assertThat(participant.getStatus()).isEqualTo(APPROVE);
    }
}
