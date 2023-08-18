package com.kgu.studywithme.common.stub;

import com.kgu.studywithme.mail.application.adapter.EmailSender;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StubEmailSender implements EmailSender {
    @Override
    public void sendParticipationApproveMail(
            final String applierEmail,
            final String nickname,
            final String studyName
    ) {
        log.info("참여 승인 메일 발송 -> 이메일: {} | 닉네임: {} | 스터디: {}", applierEmail, nickname, studyName);
    }

    @Override
    public void sendParticipationRejectMail(
            final String applierEmail,
            final String nickname,
            final String studyName,
            final String reason
    ) {
        log.info("참여 거절 메일 발송 -> 이메일: {} | 닉네임: {} | 스터디: {} | 사유: {}", applierEmail, nickname, studyName, reason);
    }

    @Override
    public void sendStudyCertificateMail(
            final String participantEmail,
            final String nickname,
            final String studyName) {
        log.info("졸업 메일 발송 -> 이메일: {} | 닉네임: {} | 스터디: {}", participantEmail, nickname, studyName);
    }
}
