package com.kgu.studywithme.member.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.usecase.command.ReportMemberUseCase;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.domain.report.Report;
import com.kgu.studywithme.member.domain.report.ReportRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
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

@DisplayName("Member -> ReportMemberService 테스트")
class ReportMemberServiceTest extends UseCaseTest {
    @InjectMocks
    private ReportMemberService memberReportService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ReportRepository reportRepository;

    private final Member memberA = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member memberB = GHOST.toMember().apply(2L, LocalDateTime.now());

    private final ReportMemberUseCase.Command command =
            new ReportMemberUseCase.Command(memberA.getId(), memberB.getId(), "report...");

    @Test
    @DisplayName("이전에 신고한 내역이 여전히 처리중이라면 중복 신고를 하지 못한다")
    void throwExceptionByPreviousReportIsStillPending() {
        // given
        given(memberRepository.isReportStillPending(any(), any())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> memberReportService.report(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.PREVIOUS_REPORT_IS_STILL_PENDING.getMessage());

        verify(memberRepository, times(1))
                .isReportStillPending(memberA.getId(), memberB.getId());
        verify(reportRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("사용자 신고에 성공한다")
    void success() {
        // given
        given(memberRepository.isReportStillPending(any(), any())).willReturn(false);

        final Report report = Report
                .createReportWithReason(memberA.getId(), memberB.getId(), "report...")
                .apply(1L, LocalDateTime.now());
        given(reportRepository.save(any())).willReturn(report);

        // when
        Long reportId = memberReportService.report(command);

        // then
        verify(memberRepository, times(1))
                .isReportStillPending(memberA.getId(), memberB.getId());
        verify(reportRepository, times(1)).save(any());
        assertThat(reportId).isEqualTo(report.getId());
    }
}
