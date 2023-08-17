package com.kgu.studywithme.mail.infrastructure;

import com.kgu.studywithme.mail.application.adapter.EmailSender;
import com.kgu.studywithme.mail.infrastructure.ses.EmailMetadata;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;
import java.util.Map;

@Profile("default")
@Component
public class DefaultEmailSender implements EmailSender {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final String serviceEmail;

    public DefaultEmailSender(
            final JavaMailSender mailSender,
            final SpringTemplateEngine templateEngine,
            @Value("${spring.mail.username}") final String serviceEmail
    ) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.serviceEmail = serviceEmail;
    }

    @Override
    public void sendParticipationApproveMail(
            final String applierEmail,
            final String nickname,
            final String studyName
    ) throws Exception {
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
    ) throws Exception {
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
    ) throws Exception {
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
    ) throws Exception {
        final MimeMessage message = mailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setSubject(subject);
        helper.setTo(email);
        helper.setFrom(new InternetAddress(serviceEmail, "여기서 구해볼래?"));
        helper.setText(mailBody, true);

        mailSender.send(message);
    }
}
