package com.kgu.studywithme.global.infrastructure.mail.event;

import com.kgu.studywithme.global.infrastructure.mail.EmailSender;
import com.kgu.studywithme.studyparticipant.event.StudyApprovedEvent;
import com.kgu.studywithme.studyparticipant.event.StudyGraduatedEvent;
import com.kgu.studywithme.studyparticipant.event.StudyRejectedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class StudyAssociatedEmailSendEventListener {
    private final EmailSender emailSender;

    @Async("emailAsyncExecutor")
    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendParticipationApproveMail(final StudyApprovedEvent event) throws Exception {
        emailSender.sendParticipationApproveMail(
                event.email(),
                event.nickname(),
                event.studyName()
        );
    }

    @Async("emailAsyncExecutor")
    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
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
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendStudyCertificateMail(final StudyGraduatedEvent event) throws Exception {
        emailSender.sendStudyCertificateMail(
                event.email(),
                event.nickname(),
                event.studyName()
        );
    }
}
