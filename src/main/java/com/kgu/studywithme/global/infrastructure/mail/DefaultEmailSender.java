package com.kgu.studywithme.global.infrastructure.mail;

import com.kgu.studywithme.global.infrastructure.mail.utils.EmailMetadata;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;

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

    public void sendParticipationApproveMail(
            final String applierEmail,
            final String nickname,
            final String studyName
    ) throws Exception {
        final Context context = new Context();
        context.setVariable("nickname", nickname);
        context.setVariable("studyName", studyName);

        final String mailBody = templateEngine.process(EmailMetadata.APPROVE_TEMPLATE, context);
        sendMail(
                EmailMetadata.PARTICIPATION_SUBJECT,
                applierEmail,
                mailBody,
                new ClassPathResource("static/images/top.png")
        );
    }

    public void sendParticipationRejectMail(
            final String applierEmail,
            final String nickname,
            final String studyName,
            final String reason
    ) throws Exception {
        final Context context = new Context();
        context.setVariable("nickname", nickname);
        context.setVariable("studyName", studyName);
        context.setVariable("reason", reason);

        final String mailBody = templateEngine.process(EmailMetadata.REJECT_TEMPLATE, context);
        sendMail(
                EmailMetadata.PARTICIPATION_SUBJECT,
                applierEmail,
                mailBody,
                new ClassPathResource("static/images/top.png")
        );
    }

    public void sendStudyCertificateMail(
            final String participantEmail,
            final String nickname,
            final String studyName
    ) throws Exception {
        final Context context = new Context();
        context.setVariable("nickname", nickname);
        context.setVariable("studyName", studyName);
        context.setVariable("completionDate", LocalDate.now().format(EmailMetadata.DATE_TIME_FORMATTER));

        final String mailBody = templateEngine.process(EmailMetadata.CERTIFICATE_TEMPLATE, context);
        sendMail(
                EmailMetadata.COMPLETION_SUBJECT,
                participantEmail,
                mailBody,
                new ClassPathResource("static/images/stamp.png")
        );
    }

    private void sendMail(
            final String subjectType,
            final String email,
            final String mailBody,
            final ClassPathResource imageResource
    ) throws Exception {
        final MimeMessage message = mailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setSubject(subjectType);
        helper.setTo(email);
        helper.setFrom(new InternetAddress(serviceEmail, "여기서 구해볼래?"));
        helper.setText(mailBody, true);
        helper.addInline("image", imageResource);

        mailSender.send(message);
    }
}
