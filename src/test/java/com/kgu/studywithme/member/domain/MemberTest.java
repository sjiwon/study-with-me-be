package com.kgu.studywithme.member.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.kgu.studywithme.common.fixture.MemberFixture.ANONYMOUS;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.LATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> 도메인 [Member] 테스트")
class MemberTest {
    @Test
    @DisplayName("Member를 생성한다")
    void constuct() {
        final Member member = JIWON.toMember();

        assertAll(
                () -> assertThat(member.getName()).isEqualTo(JIWON.getName()),
                () -> assertThat(member.getNickname()).isEqualTo(JIWON.getNickname()),
                () -> assertThat(member.getEmail()).isEqualTo(JIWON.getEmail()),
                () -> assertThat(member.getBirth()).isEqualTo(JIWON.getBirth()),
                () -> assertThat(member.getPhone()).isEqualTo(JIWON.getPhone()),
                () -> assertThat(member.getGender()).isEqualTo(JIWON.getGender()),
                () -> assertThat(member.getRegion()).isEqualTo(JIWON.getRegion()),
                () -> assertThat(member.getScore()).isEqualTo(80),
                () -> assertThat(member.isEmailOptIn()).isEqualTo(JIWON.isEmailOptIn()),
                () -> assertThat(member.getInterests()).containsExactlyInAnyOrderElementsOf(JIWON.getInterests())
        );
    }

    @Test
    @DisplayName("사용자 정보를 수정한다")
    void update() {
        // given
        final Member member = JIWON.toMember().apply(1L, LocalDateTime.now());

        // when
        member.update(
                ANONYMOUS.getNickname().getValue(),
                ANONYMOUS.getPhone(),
                ANONYMOUS.getRegion().getProvince(),
                ANONYMOUS.getRegion().getCity(),
                ANONYMOUS.isEmailOptIn(),
                ANONYMOUS.getInterests()
        );

        // then
        assertAll(
                () -> assertThat(member.getName()).isEqualTo(JIWON.getName()),
                () -> assertThat(member.getNickname()).isEqualTo(ANONYMOUS.getNickname()),
                () -> assertThat(member.getEmail()).isEqualTo(JIWON.getEmail()),
                () -> assertThat(member.getBirth()).isEqualTo(JIWON.getBirth()),
                () -> assertThat(member.getPhone()).isEqualTo(ANONYMOUS.getPhone()),
                () -> assertThat(member.getGender()).isEqualTo(JIWON.getGender()),
                () -> assertThat(member.getRegion()).isEqualTo(ANONYMOUS.getRegion()),
                () -> assertThat(member.getScore()).isEqualTo(80),
                () -> assertThat(member.isEmailOptIn()).isEqualTo(ANONYMOUS.isEmailOptIn()),
                () -> assertThat(member.getInterests()).containsExactlyInAnyOrderElementsOf(ANONYMOUS.getInterests())
        );
    }

    @Nested
    @DisplayName("사용자 점수 업데이트")
    class UpdateScore {
        private Member member;

        @BeforeEach
        void setUp() {
            member = JIWON.toMember();
        }

        @Nested
        @DisplayName("단순 출석에 대한 점수 업데이트")
        class ApplySimpleAttendance {
            @Test
            @DisplayName("출석에 대한 점수를 적용한다")
            void applyAttendance() {
                // given
                member.applyScoreByAttendanceStatus(ABSENCE); // 80 - 5 = 75

                // when
                member.applyScoreByAttendanceStatus(ATTENDANCE); // 75 + 1

                // then
                assertThat(member.getScore()).isEqualTo(76);
            }

            @Test
            @DisplayName("지각에 대한 점수를 적용한다")
            void applyLate() {
                // when
                member.applyScoreByAttendanceStatus(LATE); // 80 - 1

                // then
                assertThat(member.getScore()).isEqualTo(79);
            }

            @Test
            @DisplayName("결석에 대한 점수를 적용한다")
            void applyAbsence() {
                // when
                member.applyScoreByAttendanceStatus(ABSENCE); // 80 - 5

                // then
                assertThat(member.getScore()).isEqualTo(75);
            }
        }

        @Nested
        @DisplayName("이전 출석 정보 수정에 따른 점수 업데이트")
        class ApplyComplexAttendance {
            @Test
            @DisplayName("출석 -> 지각으로 수정함에 따라 점수를 업데이트한다")
            void updateAttendanceToLate() {
                // when
                member.applyScoreByAttendanceStatus(ATTENDANCE, LATE); // 80 - 1 - 1

                // then
                assertThat(member.getScore()).isEqualTo(78);
            }

            @Test
            @DisplayName("출석 -> 결석으로 수정함에 따라 점수를 업데이트한다")
            void updateAttendanceToAbsence() {
                // when
                member.applyScoreByAttendanceStatus(ATTENDANCE, ABSENCE); // 80 - 1 - 5

                // then
                assertThat(member.getScore()).isEqualTo(74);
            }

            @Test
            @DisplayName("지각 -> 출석으로 수정함에 따라 점수를 업데이트한다")
            void updateLateToAttendance() {
                // when
                member.applyScoreByAttendanceStatus(LATE, ATTENDANCE); // 80 + 1 + 1

                // then
                assertThat(member.getScore()).isEqualTo(82);
            }

            @Test
            @DisplayName("지각 -> 결석으로 수정함에 따라 점수를 업데이트한다")
            void updateLateToAbsence() {
                // when
                member.applyScoreByAttendanceStatus(LATE, ABSENCE); // 80 + 1 - 5

                // then
                assertThat(member.getScore()).isEqualTo(76);
            }

            @Test
            @DisplayName("결석 -> 출석으로 수정함에 따라 점수를 업데이트한다")
            void updateAbsenceToAttendance() {
                // when
                member.applyScoreByAttendanceStatus(ABSENCE, ATTENDANCE); // 80 + 5 + 1

                // then
                assertThat(member.getScore()).isEqualTo(86);
            }

            @Test
            @DisplayName("결석 -> 지각으로 수정함에 따라 점수를 업데이트한다")
            void updateAbsenceToLate() {
                // when
                member.applyScoreByAttendanceStatus(ABSENCE, LATE); // 80 + 5 - 1

                // then
                assertThat(member.getScore()).isEqualTo(84);
            }
        }
    }
}
