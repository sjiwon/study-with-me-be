package com.kgu.studywithme.mail.event;

import com.kgu.studywithme.mail.application.adapter.EmailSender;
import com.kgu.studywithme.studyparticipant.domain.event.StudyApprovedEvent;
import com.kgu.studywithme.studyparticipant.domain.event.StudyGraduatedEvent;
import com.kgu.studywithme.studyparticipant.domain.event.StudyRejectedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyAssociatedEmailSendEventListener {
    private final EmailSender emailSender;

    @Async("emailAsyncExecutor")
    @EventListener
    public void sendParticipationApproveMail(final StudyApprovedEvent event) throws Exception {
        emailSender.sendParticipationApproveMail(
                event.email(),
                event.nickname(),
                event.studyName()
        );
    }

    @Async("emailAsyncExecutor")
    @EventListener
    public void sendParticipationRejectMail(final StudyRejectedEvent event) throws Exception {
        emailSender.sendParticipationRejectMail(
                event.email(),
                event.nickname(),
                event.studyName(),
                event.reason()
        );
    }

    @Async("emailAsyncExecutor")
    @EventListener
    public void sendStudyCertificateMail(final StudyGraduatedEvent event) throws Exception {
        emailSender.sendStudyCertificateMail(
                event.email(),
                event.nickname(),
                event.studyName()
        );
    }
}
