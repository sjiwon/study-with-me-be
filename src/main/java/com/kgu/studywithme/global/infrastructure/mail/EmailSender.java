package com.kgu.studywithme.global.infrastructure.mail;

public interface EmailSender {
    void sendParticipationApproveMail(
            final String applierEmail,
            final String nickname,
            final String studyName
    ) throws Exception;

    void sendParticipationRejectMail(
            final String applierEmail,
            final String nickname,
            final String studyName,
            final String reason
    ) throws Exception;

    void sendStudyCertificateMail(
            final String participantEmail,
            final String nickname,
            final String studyName
    ) throws Exception;
}