package com.kgu.studywithme.member.application;

import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Email;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.member.domain.Region;
import com.kgu.studywithme.member.domain.report.Report;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.member.domain.report.ReportStatus.RECEIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> MemberService 테스트")
class MemberServiceTest extends ServiceTest {
    @Autowired
    private MemberService memberService;

    @Nested
    @DisplayName("사용자 신고")
    class report {
        private Member reportee;
        private Member reporter;

        @BeforeEach
        void setUp() {
            reportee = memberRepository.save(GHOST.toMember());
            reporter = memberRepository.save(JIWON.toMember());
        }

        @Test
        @DisplayName("이전에 신고한 내역이 여전히 처리중이라면 중복 신고를 하지 못한다")
        void throwExceptionByPreviousReportIsStillPending() {
            // given
            memberService.report(reportee.getId(), reporter.getId(), "참여를 안해요");

            // when - then
            assertThatThrownBy(() -> memberService.report(reportee.getId(), reporter.getId(), "10주 연속 미출석입니다"))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.PREVIOUS_REPORT_IS_STILL_PENDING.getMessage());
        }

        @Test
        @DisplayName("사용자 신고에 성공한다")
        void success() {
            // given
            final String reason = "참여를 안해요";

            // when
            Long reportId = memberService.report(reportee.getId(), reporter.getId(), reason);

            // then
            Report findReport = reportRepository.findById(reportId).orElseThrow();
            assertAll(
                    () -> assertThat(findReport.getReporteeId()).isEqualTo(reportee.getId()),
                    () -> assertThat(findReport.getReporterId()).isEqualTo(reporter.getId()),
                    () -> assertThat(findReport.getReason()).isEqualTo(reason),
                    () -> assertThat(findReport.getStatus()).isEqualTo(RECEIVE)
            );
        }
    }

    private Member createDuplicateMember(String email, String nickname, String phone) {
        return Member.createMember(
                JIWON.getName(),
                Nickname.from(nickname),
                Email.from(email),
                JIWON.getBirth(),
                phone,
                JIWON.getGender(),
                Region.of(JIWON.getProvince(), JIWON.getCity()),
                JIWON.isEmailOptIn(),
                JIWON.getInterests()
        );
    }
}
