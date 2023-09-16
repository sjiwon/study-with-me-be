package com.kgu.studywithme.memberreport.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.memberreport.application.usecase.command.ReportMemberCommand;
import com.kgu.studywithme.memberreport.domain.model.MemberReport;
import com.kgu.studywithme.memberreport.domain.repository.MemberReportRepository;
import com.kgu.studywithme.memberreport.domain.service.MemberReportHandler;
import com.kgu.studywithme.memberreport.exception.MemberReportErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("MemberReport -> ReportMemberUseCase 테스트")
class ReportMemberUseCaseTest extends UseCaseTest {
    private final MemberReportHandler memberReportHandler = mock(MemberReportHandler.class);
    private final MemberReportRepository memberReportRepository = mock(MemberReportRepository.class);
    private final ReportMemberUseCase sut = new ReportMemberUseCase(memberReportHandler, memberReportRepository);

    private final Member memberA = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member memberB = GHOST.toMember().apply(2L, LocalDateTime.now());
    private final ReportMemberCommand command = new ReportMemberCommand(memberA.getId(), memberB.getId(), "report...");

    @Test
    @DisplayName("이전에 신고한 내역이 여전히 처리중이라면 중복 신고를 하지 못한다")
    void throwExceptionByPreviousReportIsStillPending() {
        // given
        given(memberReportHandler.isPreviousReportStillPending(memberA.getId(), memberB.getId())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberReportErrorCode.PREVIOUS_REPORT_IS_STILL_PENDING.getMessage());

        assertAll(
                () -> verify(memberReportHandler, times(1)).isPreviousReportStillPending(memberA.getId(), memberB.getId()),
                () -> verify(memberReportRepository, times(0)).save(any())
        );
    }

    @Test
    @DisplayName("특정 사용자를 신고한다")
    void success() {
        // given
        given(memberReportHandler.isPreviousReportStillPending(memberA.getId(), memberB.getId())).willReturn(false);

        final MemberReport memberReport = command.toDomain().apply(1L);
        given(memberReportRepository.save(any())).willReturn(memberReport);

        // when
        final Long reportId = sut.invoke(command);

        // then
        assertAll(
                () -> verify(memberReportHandler, times(1)).isPreviousReportStillPending(memberA.getId(), memberB.getId()),
                () -> verify(memberReportRepository, times(1)).save(any()),
                () -> assertThat(reportId).isEqualTo(memberReport.getId())
        );
    }
}
