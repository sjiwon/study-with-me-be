package com.kgu.studywithme.studyparticipant.domain;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPLY;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPROVE;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StudyParticipant -> 도메인 [StudyParticipant] 테스트")
class StudyParticipantTest {
    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member member = GHOST.toMember().apply(2L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());

    @Test
    @DisplayName("사용자가 스터디에 참여 신청을 한다")
    void applyInStudy() {
        // when
        final StudyParticipant participant = StudyParticipant.applyInStudy(study.getId(), member.getId());

        // then
        assertThat(participant.getStatus()).isEqualTo(APPLY);
    }

    @Test
    @DisplayName("스터디를 생성하면 스터디 팀장은 자동으로 APPROVE상태로 참여한다")
    void applyHost() {
        // when
        final StudyParticipant participant = StudyParticipant.applyHost(study.getId(), host.getId());

        // then
        assertThat(participant.getStatus()).isEqualTo(APPROVE);
    }
}
