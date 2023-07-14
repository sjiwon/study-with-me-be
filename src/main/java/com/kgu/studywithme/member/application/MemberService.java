package com.kgu.studywithme.member.application;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.member.domain.report.Report;
import com.kgu.studywithme.member.domain.report.ReportRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import com.kgu.studywithme.member.presentation.dto.request.MemberUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberValidator memberValidator;
    private final MemberRepository memberRepository;
    private final MemberFindService memberFindService;
    private final ReportRepository reportRepository;

    @StudyWithMeWritableTransactional
    public void update(
            final Long memberId,
            final MemberUpdateRequest request
    ) {
        final Member member = memberFindService.findById(memberId);
        validateUniqueFieldsForModify(member, Nickname.from(request.nickname()), request.phone());

        member.update(
                request.nickname(),
                request.phone(),
                request.province(),
                request.city(),
                request.emailOptIn(),
                request.categories()
                        .stream()
                        .map(Category::from)
                        .collect(Collectors.toSet())
        );
    }

    private void validateUniqueFieldsForModify(
            final Member member,
            final Nickname nickname,
            final String phone
    ) {
        memberValidator.validateNicknameForModify(member.getId(), nickname);
        memberValidator.validatePhoneForModify(member.getId(), phone);
    }

    @StudyWithMeWritableTransactional
    public Long report(
            final Long reporteeId,
            final Long reporterId,
            final String reason
    ) {
        validatePreviousReportIsStillPending(reporteeId, reporterId);

        final Report report = Report.createReportWithReason(reporteeId, reporterId, reason);
        return reportRepository.save(report).getId();
    }

    private void validatePreviousReportIsStillPending(
            final Long reporteeId,
            final Long reporterId
    ) {
        if (memberRepository.isReportReceived(reporteeId, reporterId)) {
            throw StudyWithMeException.type(MemberErrorCode.PREVIOUS_REPORT_IS_STILL_PENDING);
        }
    }
}
