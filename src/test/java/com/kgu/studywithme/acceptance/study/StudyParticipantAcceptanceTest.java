package com.kgu.studywithme.acceptance.study;

import com.kgu.studywithme.common.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.*;
import static com.kgu.studywithme.common.fixture.MemberFixture.*;
import static com.kgu.studywithme.common.fixture.StudyFixture.KAFKA;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.springframework.http.HttpStatus.*;

@DisplayName("[Acceptance Test] 스터디 참여 관련 기능")
public class StudyParticipantAcceptanceTest extends AcceptanceTest {
    private String hostAccessToken;
    private Long idOfMemberA;
    private String accessTokenOfMemberA;
    private Long idOfMemberB;
    private String accessTokenOfMemberB;
    private Long studyId;
    private Long twoCapacityAndZeroPolicyStudyId; // 참여 인원 2 & 졸업 최소 출석 0

    @BeforeEach
    void setUp() {
        hostAccessToken = JIWON.회원가입_후_Google_OAuth_로그인을_진행한다().accessToken();

        idOfMemberA = GHOST.회원가입을_진행한다();
        accessTokenOfMemberA = GHOST.로그인을_진행한다().accessToken();

        idOfMemberB = ANONYMOUS.회원가입을_진행한다();
        accessTokenOfMemberB = ANONYMOUS.로그인을_진행한다().accessToken();

        studyId = SPRING.스터디를_생성한다(hostAccessToken);
        twoCapacityAndZeroPolicyStudyId = KAFKA.스터디를_생성한다(hostAccessToken);
    }

    @Nested
    @DisplayName("스터디 신청 API")
    class ApplyApi {
        @Test
        @DisplayName("스터디 팀장은 본인 스터디에 참여 신청을 할 수 없다")
        void hostCannotApply() {
            스터디_참여_신청을_한다(hostAccessToken, studyId)
                    .statusCode(FORBIDDEN.value());
        }

        @Test
        @DisplayName("스터디를 신청한다")
        void success() {
            스터디_참여_신청을_한다(accessTokenOfMemberA, studyId)
                    .statusCode(NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("스터디 신청 취소 API")
    class ApplyCancellationApi {
        @Test
        @DisplayName("참여 신청을 하지 않은 사용자는 신청 취소를 할 수 없다")
        void applierNotFound() {
            스터디_참여_신청을_취소한다(accessTokenOfMemberA, studyId)
                    .statusCode(NOT_FOUND.value());
        }

        @Test
        @DisplayName("스터디 신청을 취소한다")
        void success() {
            스터디_참여_신청을_한다(accessTokenOfMemberA, studyId);

            스터디_참여_신청을_취소한다(accessTokenOfMemberA, studyId)
                    .statusCode(NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("스터디 신청자 참여 승인 API")
    class ApplierApproveApi {
        @BeforeEach
        void setUp() {
            스터디_참여_신청을_한다(accessTokenOfMemberA, twoCapacityAndZeroPolicyStudyId);
            스터디_참여_신청을_한다(accessTokenOfMemberB, twoCapacityAndZeroPolicyStudyId);
        }

        @Test
        @DisplayName("참여 정원이 꽉 찼으면 더이상 승인할 수 없다")
        void capacityAlreadyFull() {
            스터디_신청자에_대한_참여를_승인한다(hostAccessToken, twoCapacityAndZeroPolicyStudyId, idOfMemberA);

            스터디_신청자에_대한_참여를_승인한다(hostAccessToken, twoCapacityAndZeroPolicyStudyId, idOfMemberB)
                    .statusCode(CONFLICT.value());
        }

        @Test
        @DisplayName("스터디 신청자 참여를 승인한다")
        void success() {
            스터디_신청자에_대한_참여를_승인한다(hostAccessToken, twoCapacityAndZeroPolicyStudyId, idOfMemberA)
                    .statusCode(NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("스터디 신청자 참여 거절 API")
    class ApplierRejectApi {
        @BeforeEach
        void setUp() {
            스터디_참여_신청을_한다(accessTokenOfMemberA, studyId);
            스터디_참여_신청을_한다(accessTokenOfMemberB, studyId);
        }

        @Test
        @DisplayName("스터디 신청자 참여를 거절한다")
        void success() {
            스터디_신청자에_대한_참여를_거절한다(hostAccessToken, studyId, idOfMemberA)
                    .statusCode(NO_CONTENT.value());

            스터디_신청자에_대한_참여를_거절한다(hostAccessToken, studyId, idOfMemberB)
                    .statusCode(NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("스터디 팀장 권한 위임 API")
    class DelegateHostAuthorityApi {
        @BeforeEach
        void setUp() {
            스터디_참여_신청을_한다(accessTokenOfMemberA, studyId);
        }

        @Test
        @DisplayName("스터디 참여자가 아니면 팀장 권한을 위임할 수 없다")
        void nonParticipantCannotBeHost() {
            스터디_팀장_권한을_위임한다(hostAccessToken, studyId, idOfMemberA)
                    .statusCode(CONFLICT.value());
        }

        @Test
        @DisplayName("스터디 팀장 권한을 위임한다")
        void success() {
            스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studyId, idOfMemberA);

            스터디_팀장_권한을_위임한다(hostAccessToken, studyId, idOfMemberA)
                    .statusCode(NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("스터디 참여 취소 API")
    class LeaveApi {
        @BeforeEach
        void setUp() {
            스터디_참여_신청을_한다(accessTokenOfMemberA, studyId);
            스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studyId, idOfMemberA);
        }

        @Test
        @DisplayName("스터디 팀장은 참여 취소를 할 수 없다")
        void hostCannotLeaveStudy() {
            스터디_참여를_취소한다(hostAccessToken, studyId)
                    .statusCode(CONFLICT.value());
        }

        @Test
        @DisplayName("스터디 참여를 취소한다")
        void success() {
            스터디_참여를_취소한다(accessTokenOfMemberA, studyId)
                    .statusCode(NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("스터디 졸업 API")
    class GraduateApi {
        @BeforeEach
        void setUp() {
            스터디_참여_신청을_한다(accessTokenOfMemberA, studyId);
            스터디_참여_신청을_한다(accessTokenOfMemberA, twoCapacityAndZeroPolicyStudyId);
            스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studyId, idOfMemberA);
            스터디_신청자에_대한_참여를_승인한다(hostAccessToken, twoCapacityAndZeroPolicyStudyId, idOfMemberA);
        }

        @Test
        @DisplayName("스터디 팀장은 졸업을 할 수 없다")
        void hostCannotLeaveStudy() {
            스터디를_졸업한다(hostAccessToken, studyId)
                    .statusCode(CONFLICT.value());
        }

        @Test
        @DisplayName("졸업 요건[최소 출석 횟수]를 채우지 못하면 졸업을 할 수 없다")
        void participantNotMeetGraduationPolicy() {
            스터디를_졸업한다(accessTokenOfMemberA, studyId)
                    .statusCode(CONFLICT.value());
        }

        @Test
        @DisplayName("스터디를 졸업한다")
        void success() {
            스터디를_졸업한다(accessTokenOfMemberA, twoCapacityAndZeroPolicyStudyId)
                    .statusCode(NO_CONTENT.value());
        }
    }
}
