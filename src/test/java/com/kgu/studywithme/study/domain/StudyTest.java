package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.*;
import static com.kgu.studywithme.study.domain.RecruitmentStatus.IN_PROGRESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study -> 도메인 [Study] 테스트")
class StudyTest {
    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());

    @Test
    @DisplayName("Study를 생성한다")
    void construct() {
        final Study onlineStudy = SPRING.toOnlineStudy(host.getId());
        final Study offlineStudy = TOSS_INTERVIEW.toOfflineStudy(host.getId());

        assertAll(
                () -> assertThat(onlineStudy.getNameValue()).isEqualTo(SPRING.getName()),
                () -> assertThat(onlineStudy.getDescriptionValue()).isEqualTo(SPRING.getDescription()),
                () -> assertThat(onlineStudy.getCategory()).isEqualTo(SPRING.getCategory()),
                () -> assertThat(onlineStudy.getCapacity()).isEqualTo(SPRING.getCapacity()),
                () -> assertThat(onlineStudy.getThumbnail()).isEqualTo(SPRING.getThumbnail()),
                () -> assertThat(onlineStudy.getType()).isEqualTo(SPRING.getType()),
                () -> assertThat(onlineStudy.getLocation()).isNull(),
                () -> assertThat(onlineStudy.getRecruitmentStatus()).isEqualTo(IN_PROGRESS),
                () -> assertThat(onlineStudy.getMinimumAttendanceForGraduation()).isEqualTo(SPRING.getMinimumAttendanceForGraduation()),
                () -> assertThat(onlineStudy.getGraduationPolicy().getUpdateChance()).isEqualTo(3),
                () -> assertThat(onlineStudy.isTerminated()).isFalse(),
                () -> assertThat(onlineStudy.getHashtags()).containsExactlyInAnyOrderElementsOf(SPRING.getHashtags()),

                () -> assertThat(offlineStudy.getNameValue()).isEqualTo(TOSS_INTERVIEW.getName()),
                () -> assertThat(offlineStudy.getDescriptionValue()).isEqualTo(TOSS_INTERVIEW.getDescription()),
                () -> assertThat(offlineStudy.getCategory()).isEqualTo(TOSS_INTERVIEW.getCategory()),
                () -> assertThat(offlineStudy.getCapacity()).isEqualTo(TOSS_INTERVIEW.getCapacity()),
                () -> assertThat(offlineStudy.getThumbnail()).isEqualTo(TOSS_INTERVIEW.getThumbnail()),
                () -> assertThat(offlineStudy.getType()).isEqualTo(TOSS_INTERVIEW.getType()),
                () -> assertThat(offlineStudy.getLocation()).isEqualTo(TOSS_INTERVIEW.getLocation()),
                () -> assertThat(offlineStudy.getRecruitmentStatus()).isEqualTo(IN_PROGRESS),
                () -> assertThat(offlineStudy.getMinimumAttendanceForGraduation()).isEqualTo(TOSS_INTERVIEW.getMinimumAttendanceForGraduation()),
                () -> assertThat(offlineStudy.getGraduationPolicy().getUpdateChance()).isEqualTo(3),
                () -> assertThat(offlineStudy.isTerminated()).isFalse(),
                () -> assertThat(offlineStudy.getHashtags()).containsExactlyInAnyOrderElementsOf(TOSS_INTERVIEW.getHashtags())
        );
    }

    @Test
    @DisplayName("Study 정보를 수정한다")
    void update() {
        // given
        final Study onlineStudy = JAPANESE.toOnlineStudy(host.getId());
        final Study offlineStudy = TOSS_INTERVIEW.toOnlineStudy(host.getId());

        // when
        onlineStudy.update(
                StudyName.from(CHINESE.name()),
                Description.from(CHINESE.getDescription()),
                Capacity.from(CHINESE.getCapacity()),
                CHINESE.getCategory(),
                CHINESE.getThumbnail(),
                CHINESE.getType(),
                null, null,
                IN_PROGRESS,
                CHINESE.getMinimumAttendanceForGraduation(),
                CHINESE.getHashtags()
        );

        offlineStudy.update(
                StudyName.from(KAKAO_INTERVIEW.name()),
                Description.from(KAKAO_INTERVIEW.getDescription()),
                Capacity.from(KAKAO_INTERVIEW.getCapacity()),
                KAKAO_INTERVIEW.getCategory(),
                KAKAO_INTERVIEW.getThumbnail(),
                KAKAO_INTERVIEW.getType(),
                KAKAO_INTERVIEW.getLocation().getProvince(),
                KAKAO_INTERVIEW.getLocation().getCity(),
                IN_PROGRESS,
                KAKAO_INTERVIEW.getMinimumAttendanceForGraduation(),
                KAKAO_INTERVIEW.getHashtags()
        );

        // then
        assertAll(
                () -> assertThat(onlineStudy.getNameValue()).isEqualTo(CHINESE.name()),
                () -> assertThat(onlineStudy.getDescriptionValue()).isEqualTo(CHINESE.getDescription()),
                () -> assertThat(onlineStudy.getCategory()).isEqualTo(CHINESE.getCategory()),
                () -> assertThat(onlineStudy.getCapacity()).isEqualTo(CHINESE.getCapacity()),
                () -> assertThat(onlineStudy.getThumbnail()).isEqualTo(CHINESE.getThumbnail()),
                () -> assertThat(onlineStudy.getType()).isEqualTo(CHINESE.getType()),
                () -> assertThat(onlineStudy.getLocation()).isNull(),
                () -> assertThat(onlineStudy.getRecruitmentStatus()).isEqualTo(IN_PROGRESS),
                () -> assertThat(onlineStudy.getMinimumAttendanceForGraduation()).isEqualTo(CHINESE.getMinimumAttendanceForGraduation()),
                () -> assertThat(onlineStudy.getGraduationPolicy().getUpdateChance()).isEqualTo(2),
                () -> assertThat(onlineStudy.getHashtags()).containsExactlyInAnyOrderElementsOf(CHINESE.getHashtags()),

                () -> assertThat(offlineStudy.getNameValue()).isEqualTo(KAKAO_INTERVIEW.name()),
                () -> assertThat(offlineStudy.getDescriptionValue()).isEqualTo(KAKAO_INTERVIEW.getDescription()),
                () -> assertThat(offlineStudy.getCategory()).isEqualTo(KAKAO_INTERVIEW.getCategory()),
                () -> assertThat(offlineStudy.getThumbnail()).isEqualTo(KAKAO_INTERVIEW.getThumbnail()),
                () -> assertThat(offlineStudy.getType()).isEqualTo(KAKAO_INTERVIEW.getType()),
                () -> assertThat(offlineStudy.getLocation()).isEqualTo(KAKAO_INTERVIEW.getLocation()),
                () -> assertThat(offlineStudy.getRecruitmentStatus()).isEqualTo(IN_PROGRESS),
                () -> assertThat(offlineStudy.getMinimumAttendanceForGraduation()).isEqualTo(KAKAO_INTERVIEW.getMinimumAttendanceForGraduation()),
                () -> assertThat(offlineStudy.getGraduationPolicy().getUpdateChance()).isEqualTo(2),
                () -> assertThat(offlineStudy.getHashtags()).containsExactlyInAnyOrderElementsOf(KAKAO_INTERVIEW.getHashtags())
        );
    }

    @Test
    @DisplayName("스터디 팀장인지 확인한다")
    void isHost() {
        // given
        final Study study = SPRING.toOnlineStudy(host.getId());

        // when
        final boolean actual1 = study.isHost(host.getId());
        final boolean actual2 = study.isHost(host.getId() + 100L);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("스터디 팀장 권한을 위임한다 -> 졸업 요건 수정 횟수 초기화")
    void delegateHostAuthority() {
        // given
        final Study study = SPRING.toOnlineStudy(host.getId());

        ReflectionTestUtils.setField(study.getGraduationPolicy(), "updateChance", 1);
        assertThat(study.getGraduationPolicy().getUpdateChance()).isEqualTo(1);

        // when
        study.delegateHostAuthority(12345L);

        // then
        assertAll(
                () -> assertThat(study.getHostId()).isEqualTo(12345L),
                () -> assertThat(study.getGraduationPolicy().getUpdateChance()).isEqualTo(3)
        );
    }

    @Test
    @DisplayName("스터디 모집이 마감되었는지 확인한다")
    void isRecruitmentComplete_recruitingEnd() {
        // given
        final Study study = SPRING.toOnlineStudy(host.getId());

        /* 모집 중 */
        assertThat(study.isRecruitmentComplete()).isFalse();

        /* 모집 완료 */
        study.recruitingEnd();
        assertThat(study.isRecruitmentComplete()).isTrue();
    }

    @Test
    @DisplayName("스터디를 종료한다")
    void terminateStudy() {
        // given
        final Study study = SPRING.toOnlineStudy(host.getId());
        assertThat(study.isTerminated()).isFalse();

        // when
        study.terminate();

        // then
        assertThat(study.isTerminated()).isTrue();
    }

    @Test
    @DisplayName("스터디 정원이 꽉 찼는지 확인한다")
    void isCapacityFull() {
        // given
        final Study study = SPRING.toOnlineStudy(host.getId());

        // when
        final boolean actual1 = study.isCapacityFull(study.getCapacity() - 1);
        final boolean actual2 = study.isCapacityFull(study.getCapacity());
        final boolean actual3 = study.isCapacityFull(study.getCapacity() + 1);

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isTrue()
        );
    }

    @Test
    @DisplayName("스터디 참여자가 졸업 요건[최소 출석 횟수]를 만족했는지 확인한다")
    void isParticipantMeetGraduationPolicy() {
        // given
        final Study study = SPRING.toOnlineStudy(host.getId());
        final int minimumAttendanceForGraduation = study.getMinimumAttendanceForGraduation();

        // when
        final boolean actual1 = study.isParticipantMeetGraduationPolicy(minimumAttendanceForGraduation - 1);
        final boolean actual2 = study.isParticipantMeetGraduationPolicy(minimumAttendanceForGraduation);
        final boolean actual3 = study.isParticipantMeetGraduationPolicy(minimumAttendanceForGraduation);

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isTrue()
        );
    }
}
