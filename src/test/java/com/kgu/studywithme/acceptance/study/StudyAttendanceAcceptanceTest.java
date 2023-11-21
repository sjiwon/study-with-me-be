package com.kgu.studywithme.acceptance.study;

import com.kgu.studywithme.common.AcceptanceTest;
import com.kgu.studywithme.common.config.DatabaseCleanerEachCallbackExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.사용자에_대한_해당_주차_출석_정보를_수정한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_신청자에_대한_참여를_승인한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_주차를_생성한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_참여_신청을_한다;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ATTENDANCE;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 스터디 출석 관련 기능")
public class StudyAttendanceAcceptanceTest extends AcceptanceTest {
    private String hostAccessToken;
    private Long memberId;
    private Long studyId;

    @BeforeEach
    void setUp() {
        hostAccessToken = JIWON.회원가입_후_Google_OAuth_로그인을_진행하고_AccessToken을_추출한다();
        memberId = GHOST.회원가입을_진행한다();
        final String memberAccessToken = GHOST.로그인을_진행하고_AccessToken을_추출한다();

        studyId = SPRING.스터디를_생성한다(hostAccessToken);
        스터디_참여_신청을_한다(memberAccessToken, studyId);
        스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studyId, memberId);
    }

    @Nested
    @DisplayName("스터디 출석 정보 수정 API")
    class ManualAttendanceApi {
        @Test
        @DisplayName("사용자에 대한 해당 주차 출석 정보를 수정한다")
        void success() {
            스터디_주차를_생성한다(hostAccessToken, studyId, STUDY_WEEKLY_1);

            사용자에_대한_해당_주차_출석_정보를_수정한다(hostAccessToken, studyId, memberId, STUDY_WEEKLY_1.getWeek(), ATTENDANCE)
                    .statusCode(NO_CONTENT.value());
        }
    }
}
