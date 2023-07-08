package com.kgu.studywithme.member.service;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.controller.dto.request.MemberUpdateRequest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.member.domain.report.Report;
import com.kgu.studywithme.member.domain.report.ReportRepository;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberValidator memberValidator;
    private final MemberRepository memberRepository;
    private final MemberFindService memberFindService;
    private final ReportRepository reportRepository;

    @Transactional
    public Long signUp(final Member member) {
        validateUniqueFields(member);
        return memberRepository.save(member).getId();
    }

    private void validateUniqueFields(final Member member) {
        memberValidator.validateEmail(member.getEmail());
        memberValidator.validateNickname(member.getNickname());
        memberValidator.validatePhone(member.getPhone());
    }

    @Transactional
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

    @Transactional
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
