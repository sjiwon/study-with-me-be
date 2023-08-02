package com.kgu.studywithme.acceptance.study;

import com.kgu.studywithme.common.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.*;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_2;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@DisplayName("[Acceptance Test] 스터디 주차 관련 기능")
public class StudyWeeklyAcceptanceTest extends AcceptanceTest {
    private String hostAccessToken;
    private Long studyId;

    @BeforeEach
    void setUp() {
        hostAccessToken = JIWON.회원가입_후_Google_OAuth_로그인을_진행한다().accessToken();
        studyId = SPRING.스터디를_생성한다(hostAccessToken);
    }

    @Nested
    @DisplayName("스터디 주차 생성 API")
    class CreateWeekly {
        @Test
        @DisplayName("스터디 주차를 생성한다")
        void success() {
            스터디_주차를_생성한다(hostAccessToken, studyId, STUDY_WEEKLY_1)
                    .statusCode(NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("스터디 주차 수정 API")
    class UpdateWeekly {
        @Test
        @DisplayName("생성한 스터디 주차 정보를 수정한다")
        void success() {
            스터디_주차를_생성한다(hostAccessToken, studyId, STUDY_WEEKLY_1);

            스터디_주차를_수정한다(hostAccessToken, studyId, STUDY_WEEKLY_1.getWeek(), STUDY_WEEKLY_2)
                    .statusCode(NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("스터디 주차 삭제 API")
    class DeleteWeekly {
        @BeforeEach
        void setUp() {
            스터디_주차를_생성한다(hostAccessToken, studyId, STUDY_WEEKLY_1);
            스터디_주차를_생성한다(hostAccessToken, studyId, STUDY_WEEKLY_2);
        }

        @Test
        @DisplayName("가장 최신 주차만 삭제할 수 있다")
        void onlyLatestWeeklyCanDelete() {
            스터디_주차를_삭제한다(hostAccessToken, studyId, STUDY_WEEKLY_1.getWeek())
                    .statusCode(CONFLICT.value());
        }

        @Test
        @DisplayName("최신 주차를 삭제한다")
        void success() {
            스터디_주차를_삭제한다(hostAccessToken, studyId, STUDY_WEEKLY_2.getWeek())
                    .statusCode(NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("스터디 주차별 과제 제출 API")
    class SubmitWeeklyAssignment {
        @BeforeEach
        void setUp() {
            스터디_주차를_생성한다(hostAccessToken, studyId, STUDY_WEEKLY_1);
        }

        @Test
        @DisplayName("해당 주차에 과제를 제출한다")
        void success() {
            해당_주차에_과제를_제출한다(hostAccessToken, studyId, STUDY_WEEKLY_1.getWeek())
                    .statusCode(NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("스터디 주차별 제출한 과제 수정 API")
    class EditSubmittedWeeklyAssignment {
        @BeforeEach
        void setUp() {
            스터디_주차를_생성한다(hostAccessToken, studyId, STUDY_WEEKLY_1);
        }

        @Test
        @DisplayName("해당 주차에 제출한 과제를 수정한다")
        void success() {
            해당_주차에_과제를_제출한다(hostAccessToken, studyId, STUDY_WEEKLY_1.getWeek());

            해당_주차에_제출한_과제를_수정한다(hostAccessToken, studyId, STUDY_WEEKLY_1.getWeek())
                    .statusCode(NO_CONTENT.value());
        }
    }
}
