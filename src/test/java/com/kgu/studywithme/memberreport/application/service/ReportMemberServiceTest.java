package com.kgu.studywithme.memberreport.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.memberreport.application.usecase.command.ReportMemberUseCase;
import com.kgu.studywithme.memberreport.domain.MemberReport;
import com.kgu.studywithme.memberreport.domain.MemberReportRepository;
import com.kgu.studywithme.memberreport.exception.MemberReportErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("MemberReport -> ReportMemberService 테스트")
class ReportMemberServiceTest extends UseCaseTest {
    @InjectMocks
    private ReportMemberService reportMemberService;

    @Mock
    private MemberReportRepository memberReportRepository;

    private final Member memberA = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member memberB = GHOST.toMember().apply(2L, LocalDateTime.now());

    private final ReportMemberUseCase.Command command =
            new ReportMemberUseCase.Command(memberA.getId(), memberB.getId(), "report...");

    @Test
    @DisplayName("이전에 신고한 내역이 여전히 처리중이라면 중복 신고를 하지 못한다")
    void throwExceptionByPreviousReportIsStillPending() {
        // given
        given(memberReportRepository.isReportStillPending(any(), any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> reportMemberService.report(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReportErrorCode.PREVIOUS_REPORT_IS_STILL_PENDING.getMessage());

        verify(memberReportRepository, times(1))
                .isReportStillPending(memberA.getId(), memberB.getId());
        verify(memberReportRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("사용자 신고에 성공한다")
    void success() {
        // given
        given(memberReportRepository.isReportStillPending(any(), any())).willReturn(false);

        final MemberReport memberReport = MemberReport
                .createReportWithReason(memberA.getId(), memberB.getId(), "report...")
                .apply(1L, LocalDateTime.now());
        given(memberReportRepository.save(any())).willReturn(memberReport);

        // when
        Long reportId = reportMemberService.report(command);

        // then
        verify(memberReportRepository, times(1))
                .isReportStillPending(memberA.getId(), memberB.getId());
        verify(memberReportRepository, times(1)).save(any());
        assertThat(reportId).isEqualTo(memberReport.getId());
    }
}