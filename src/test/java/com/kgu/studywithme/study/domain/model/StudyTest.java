package com.kgu.studywithme.study.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.CHINESE;
import static com.kgu.studywithme.common.fixture.StudyFixture.JAPANESE;
import static com.kgu.studywithme.common.fixture.StudyFixture.KAKAO_INTERVIEW;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyFixture.TOSS_INTERVIEW;
import static com.kgu.studywithme.study.domain.model.RecruitmentStatus.ON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study -> 도메인 [Study] 테스트")
class StudyTest extends ParallelTest {
    private final Member host = JIWON.toMember().apply(1L);
    private final Member other = GHOST.toMember().apply(2L);

    @Test
    @DisplayName("Study를 생성한다")
    void construct() {
        final Study onlineStudy = SPRING.toStudy(host);
        final Study offlineStudy = TOSS_INTERVIEW.toStudy(host);

        assertAll(
                () -> assertThat(onlineStudy.getName()).isEqualTo(SPRING.getName()),
                () -> assertThat(onlineStudy.getDescription()).isEqualTo(SPRING.getDescription()),
                () -> assertThat(onlineStudy.getCategory()).isEqualTo(SPRING.getCategory()),
                () -> assertThat(onlineStudy.getCapacity()).isEqualTo(SPRING.getCapacity()),
                () -> assertThat(onlineStudy.getParticipants()).isEqualTo(1),
                () -> assertThat(onlineStudy.getThumbnail()).isEqualTo(SPRING.getThumbnail()),
                () -> assertThat(onlineStudy.getType()).isEqualTo(SPRING.getType()),
                () -> assertThat(onlineStudy.getLocation()).isNull(),
                () -> assertThat(onlineStudy.getRecruitmentStatus()).isEqualTo(ON),
                () -> assertThat(onlineStudy.getGraduationPolicy().getMinimumAttendance()).isEqualTo(SPRING.getMinimumAttendanceForGraduation()),
                () -> assertThat(onlineStudy.getGraduationPolicy().getUpdateChance()).isEqualTo(GraduationPolicy.DEFAULT_UPDATE_CHANCE),
                () -> assertThat(onlineStudy.isTerminated()).isFalse(),
                () -> assertThat(onlineStudy.getHashtags()).containsExactlyInAnyOrderElementsOf(SPRING.getHashtags()),
                () -> assertThat(onlineStudy.getFavoriteCount()).isEqualTo(0),
                () -> assertThat(onlineStudy.getReviewCount()).isEqualTo(0),

                () -> assertThat(offlineStudy.getName()).isEqualTo(TOSS_INTERVIEW.getName()),
                () -> assertThat(offlineStudy.getDescription()).isEqualTo(TOSS_INTERVIEW.getDescription()),
                () -> assertThat(offlineStudy.getCategory()).isEqualTo(TOSS_INTERVIEW.getCategory()),
                () -> assertThat(offlineStudy.getCapacity()).isEqualTo(TOSS_INTERVIEW.getCapacity()),
                () -> assertThat(offlineStudy.getParticipants()).isEqualTo(1),
                () -> assertThat(offlineStudy.getThumbnail()).isEqualTo(TOSS_INTERVIEW.getThumbnail()),
                () -> assertThat(offlineStudy.getType()).isEqualTo(TOSS_INTERVIEW.getType()),
                () -> assertThat(offlineStudy.getLocation()).isEqualTo(TOSS_INTERVIEW.getLocation()),
                () -> assertThat(offlineStudy.getRecruitmentStatus()).isEqualTo(ON),
                () -> assertThat(offlineStudy.getGraduationPolicy().getMinimumAttendance()).isEqualTo(TOSS_INTERVIEW.getMinimumAttendanceForGraduation()),
                () -> assertThat(offlineStudy.getGraduationPolicy().getUpdateChance()).isEqualTo(GraduationPolicy.DEFAULT_UPDATE_CHANCE),
                () -> assertThat(offlineStudy.isTerminated()).isFalse(),
                () -> assertThat(offlineStudy.getHashtags()).containsExactlyInAnyOrderElementsOf(TOSS_INTERVIEW.getHashtags()),
                () -> assertThat(offlineStudy.getFavoriteCount()).isEqualTo(0),
                () -> assertThat(offlineStudy.getReviewCount()).isEqualTo(0)
        );
    }

    @Test
    @DisplayName("Study 정보를 수정한다")
    void update() {
        // given
        final Study onlineStudy = JAPANESE.toStudy(host);
        final Study offlineStudy = TOSS_INTERVIEW.toStudy(host);

        // when
        onlineStudy.update(
                CHINESE.getName(),
                CHINESE.getDescription(),
                CHINESE.getCapacity().getValue(),
                CHINESE.getCategory(),
                CHINESE.getThumbnail(),
                CHINESE.getType(),
                null, null,
                ON,
                CHINESE.getMinimumAttendanceForGraduation(),
                CHINESE.getHashtags()
        );

        offlineStudy.update(
                KAKAO_INTERVIEW.getName(),
                KAKAO_INTERVIEW.getDescription(),
                KAKAO_INTERVIEW.getCapacity().getValue(),
                KAKAO_INTERVIEW.getCategory(),
                KAKAO_INTERVIEW.getThumbnail(),
                KAKAO_INTERVIEW.getType(),
                KAKAO_INTERVIEW.getLocation().getProvince(),
                KAKAO_INTERVIEW.getLocation().getCity(),
                ON,
                KAKAO_INTERVIEW.getMinimumAttendanceForGraduation(),
                KAKAO_INTERVIEW.getHashtags()
        );

        // then
        assertAll(
                () -> assertThat(onlineStudy.getName()).isEqualTo(CHINESE.getName()),
                () -> assertThat(onlineStudy.getDescription()).isEqualTo(CHINESE.getDescription()),
                () -> assertThat(onlineStudy.getCategory()).isEqualTo(CHINESE.getCategory()),
                () -> assertThat(onlineStudy.getCapacity()).isEqualTo(CHINESE.getCapacity()),
                () -> assertThat(onlineStudy.getParticipants()).isEqualTo(1), // host
                () -> assertThat(onlineStudy.getThumbnail()).isEqualTo(CHINESE.getThumbnail()),
                () -> assertThat(onlineStudy.getType()).isEqualTo(CHINESE.getType()),
                () -> assertThat(onlineStudy.getLocation()).isNull(),
                () -> assertThat(onlineStudy.getRecruitmentStatus()).isEqualTo(ON),
                () -> assertThat(onlineStudy.getGraduationPolicy().getMinimumAttendance()).isEqualTo(CHINESE.getMinimumAttendanceForGraduation()),
                () -> assertThat(onlineStudy.getGraduationPolicy().getUpdateChance()).isEqualTo(GraduationPolicy.DEFAULT_UPDATE_CHANCE - 1),
                () -> assertThat(onlineStudy.getHashtags()).containsExactlyInAnyOrderElementsOf(CHINESE.getHashtags()),

                () -> assertThat(offlineStudy.getName()).isEqualTo(KAKAO_INTERVIEW.getName()),
                () -> assertThat(offlineStudy.getDescription()).isEqualTo(KAKAO_INTERVIEW.getDescription()),
                () -> assertThat(offlineStudy.getCategory()).isEqualTo(KAKAO_INTERVIEW.getCategory()),
                () -> assertThat(offlineStudy.getCapacity()).isEqualTo(KAKAO_INTERVIEW.getCapacity()),
                () -> assertThat(offlineStudy.getParticipants()).isEqualTo(1), // host
                () -> assertThat(offlineStudy.getThumbnail()).isEqualTo(KAKAO_INTERVIEW.getThumbnail()),
                () -> assertThat(offlineStudy.getType()).isEqualTo(KAKAO_INTERVIEW.getType()),
                () -> assertThat(offlineStudy.getLocation()).isEqualTo(KAKAO_INTERVIEW.getLocation()),
                () -> assertThat(offlineStudy.getRecruitmentStatus()).isEqualTo(ON),
                () -> assertThat(offlineStudy.getGraduationPolicy().getMinimumAttendance()).isEqualTo(KAKAO_INTERVIEW.getMinimumAttendanceForGraduation()),
                () -> assertThat(offlineStudy.getGraduationPolicy().getUpdateChance()).isEqualTo(GraduationPolicy.DEFAULT_UPDATE_CHANCE - 1),
                () -> assertThat(offlineStudy.getHashtags()).containsExactlyInAnyOrderElementsOf(KAKAO_INTERVIEW.getHashtags())
        );
    }

    @Test
    @DisplayName("스터디 팀장인지 확인한다")
    void isHost() {
        // given
        final Study study = SPRING.toStudy(host);

        // when
        final boolean actual1 = study.isHost(host);
        final boolean actual2 = study.isHost(other);

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
        final Study study = SPRING.toStudy(host);
        ReflectionTestUtils.setField(study.getGraduationPolicy(), "updateChance", 1);

        assertAll(
                () -> assertThat(study.isHost(host)).isTrue(),
                () -> assertThat(study.isHost(other)).isFalse(),
                () -> assertThat(study.getGraduationPolicy().getUpdateChance()).isEqualTo(1)
        );

        // when
        study.delegateHostAuthority(other);

        // then
        assertAll(
                () -> assertThat(study.isHost(host)).isFalse(),
                () -> assertThat(study.isHost(other)).isTrue(),
                () -> assertThat(study.getGraduationPolicy().getUpdateChance()).isEqualTo(GraduationPolicy.DEFAULT_UPDATE_CHANCE)
        );
    }

    @Test
    @DisplayName("스터디를 종료한다")
    void terminate() {
        // given
        final Study study = SPRING.toStudy(host);
        assertThat(study.isTerminated()).isFalse();

        // when
        study.terminate();

        // then
        assertThat(study.isTerminated()).isTrue();
    }

    @Nested
    @DisplayName("신청자 참여 승인 / 참여자 참여 취소 or 졸업")
    class AddOrRemoveParticipant {
        private Study study;

        @BeforeEach
        void setUp() {
            study = JAPANESE.toStudy(host);
        }

        @Nested
        @DisplayName("신청자 참여 승인")
        class AddParticipant {
            @Test
            @DisplayName("스터디 정원이 가득 찼음에 따라 더이상 신청자를 승인할 수 없다")
            void throwExceptionByCapacityAlreadyFull() {
                // given
                final int capacity = study.getCapacity().getValue();
                for (int i = 0; i < capacity - 1; i++) {
                    study.addParticipant();
                }

                // when - then
                assertThatThrownBy(study::addParticipant)
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyParticipantErrorCode.STUDY_CAPACITY_ALREADY_FULL.getMessage());
            }

            @Test
            @DisplayName("신청자에 대해서 참여를 승인한다")
            void success() {
                // when
                study.addParticipant();

                // then
                assertThat(study.getParticipants()).isEqualTo(2); // host + 1
            }
        }

        @Test
        @DisplayName("참여자가 스터디를 나간다 (취소 or 졸업)")
        void removeParticipant() {
            // given
            study.addParticipant();
            study.addParticipant();
            study.addParticipant(); // participants = 4
            assertThat(study.getParticipants()).isEqualTo(4);

            // when
            study.removeParticipant();

            // then
            assertThat(study.getParticipants()).isEqualTo(4 - 1);
        }
    }

    @Test
    @DisplayName("스터디 참여자가 졸업 요건[최소 출석 횟수]를 만족했는지 확인한다")
    void isParticipantMeetGraduationPolicy() {
        // given
        final Study study = SPRING.toStudy(host);
        final int minimumAttendanceForGraduation = study.getGraduationPolicy().getMinimumAttendance();

        // when
        final boolean actual1 = study.isParticipantMeetGraduationPolicy(minimumAttendanceForGraduation - 1);
        final boolean actual2 = study.isParticipantMeetGraduationPolicy(minimumAttendanceForGraduation);
        final boolean actual3 = study.isParticipantMeetGraduationPolicy(minimumAttendanceForGraduation + 1);

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isTrue()
        );
    }
}
