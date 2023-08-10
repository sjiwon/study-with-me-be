package com.kgu.studywithme.global.infrastructure.mail;

import com.kgu.studywithme.global.infrastructure.mail.utils.EmailMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.time.LocalDate;
import java.util.Map;

@Profile("prod")
@Component
public class AwsSESEmailSender implements EmailSender {
    private final SesClient sesClient;
    private final SpringTemplateEngine templateEngine;
    private final String serviceEmail;

    public AwsSESEmailSender(
            final SesClient sesClient,
            final SpringTemplateEngine templateEngine,
            @Value("${spring.mail.username}") final String serviceEmail
    ) {
        this.sesClient = sesClient;
        this.templateEngine = templateEngine;
        this.serviceEmail = serviceEmail;
    }

    @Override
    public void sendParticipationApproveMail(
            final String applierEmail,
            final String nickname,
            final String studyName
    ) {
        final Map<String, Object> variables = Map.of(
                "nickname", nickname,
                "studyName", studyName
        );
        final Context context = createTemplateContext(variables);

        final String mailBody = templateEngine.process(EmailMetadata.APPROVE_TEMPLATE, context);
        sendMail(
                EmailMetadata.PARTICIPATION_SUBJECT,
                applierEmail,
                mailBody
        );
    }

    @Override
    public void sendParticipationRejectMail(
            final String applierEmail,
            final String nickname,
            final String studyName,
            final String reason
    ) {
        final Map<String, Object> variables = Map.of(
                "nickname", nickname,
                "studyName", studyName,
                "reason", reason
        );
        final Context context = createTemplateContext(variables);

        final String mailBody = templateEngine.process(EmailMetadata.REJECT_TEMPLATE, context);
        sendMail(
                EmailMetadata.PARTICIPATION_SUBJECT,
                applierEmail,
                mailBody
        );
    }

    @Override
    public void sendStudyCertificateMail(
            final String participantEmail,
            final String nickname,
            final String studyName
    ) {
        final Map<String, Object> variables = Map.of(
                "nickname", nickname,
                "studyName", studyName,
                "completionDate", LocalDate.now().format(EmailMetadata.DATE_TIME_FORMATTER)
        );
        final Context context = createTemplateContext(variables);

        final String mailBody = templateEngine.process(EmailMetadata.CERTIFICATE_TEMPLATE, context);
        sendMail(
                EmailMetadata.COMPLETION_SUBJECT,
                participantEmail,
                mailBody
        );
    }

    private Context createTemplateContext(final Map<String, Object> variables) {
        final Context context = new Context();
        context.setVariables(variables);
        return context;
    }

    private void sendMail(
            final String subject,
            final String email,
            final String mailBody
    ) {
        sesClient.sendEmail(createEmailRequest(subject, email, mailBody));
    }

    private SendEmailRequest createEmailRequest(
            final String subject,
            final String email,
            final String mailBody
    ) {
        return SendEmailRequest.builder()
                .source(serviceEmail)
                .destination(createDestination(email))
                .message(createMessage(subject, mailBody))
                .build();
    }

    private Destination createDestination(final String recipient) {
        return Destination.builder()
                .toAddresses(recipient)
                .build();
    }

    private Message createMessage(
            final String subject,
            final String body
    ) {
        final Content mailSubject = createContent(subject);
        final Body mailBody = Body.builder()
                .html(createContent(body))
                .build();

        return Message.builder()
                .subject(mailSubject)
                .body(mailBody)
                .build();
    }

    private Content createContent(final String data) {
        return Content.builder()
                .charset("UTF-8")
                .data(data)
                .build();
    }
}
