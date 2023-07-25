package com.kgu.studywithme.global.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUserResponse;
import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kgu.studywithme.global.infrastructure.slack.SlackMetadata.*;
import static com.slack.api.webhook.WebhookPayloads.payload;

@Slf4j
@RestControllerAdvice
public class ApiGlobalExceptionHandler {
    private static final Slack SLACK_CLIENT = Slack.getInstance();

    private final ObjectMapper objectMapper;
    private final String slackWebhookUrl;

    public ApiGlobalExceptionHandler(
            final ObjectMapper objectMapper,
            @Value("${slack.webhook.url}") final String slackWebhookUrl
    ) {
        this.objectMapper = objectMapper;
        this.slackWebhookUrl = slackWebhookUrl;
    }

    @ExceptionHandler(StudyWithMeException.class)
    public ResponseEntity<ErrorResponse> studyWithMeException(final StudyWithMeException e) {
        final ErrorCode code = e.getCode();
        logging(code, e);

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    @ExceptionHandler(StudyWithMeOAuthException.class)
    public ResponseEntity<OAuthUserResponse> studyWithMeOAuthException(final StudyWithMeOAuthException e) {
        final OAuthUserResponse response = e.getResponse();
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /**
     * 요청 파라미터 Validation 전용 ExceptionHandler
     */
    @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> unsatisfiedServletRequestParameterException(
            final UnsatisfiedServletRequestParameterException e
    ) {
        return convert(GlobalErrorCode.VALIDATION_ERROR, e);
    }

    /**
     * 요청 데이터 Validation 전용 ExceptionHandler (@RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(
            final MethodArgumentNotValidException e
    ) throws JsonProcessingException {
        final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        return convert(GlobalErrorCode.VALIDATION_ERROR, extractErrorMessage(fieldErrors), e);
    }

    /**
     * 요청 데이터 Validation 전용 ExceptionHandler (@ModelAttribute)
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> bindException(final BindException e) throws JsonProcessingException {
        final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        return convert(GlobalErrorCode.VALIDATION_ERROR, extractErrorMessage(fieldErrors), e);
    }

    private String extractErrorMessage(final List<FieldError> fieldErrors) throws JsonProcessingException {
        if (fieldErrors.size() == 1) {
            return fieldErrors.get(0).getDefaultMessage();
        }

        final Map<String, String> errors = new HashMap<>();
        for (FieldError error : fieldErrors) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return objectMapper.writeValueAsString(errors);
    }

    /**
     * 존재하지 않는 Endpoint 전용 ExceptionHandler
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> noHandlerFoundException(final NoHandlerFoundException e) {
        return convert(GlobalErrorCode.NOT_SUPPORTED_URI_ERROR, e);
    }

    /**
     * Method Argument Exception 전용 ExceptionHandler
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> methodArgumentTypeMismatchException(
            final MethodArgumentTypeMismatchException e
    ) {
        return convert(GlobalErrorCode.NOT_SUPPORTED_URI_ERROR, e);
    }

    /**
     * HTTP Request Method 오류 전용 ExceptionHandler
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> httpRequestMethodNotSupportedException(
            final HttpRequestMethodNotSupportedException e
    ) {
        return convert(GlobalErrorCode.NOT_SUPPORTED_METHOD_ERROR, e);
    }

    /**
     * MediaType 전용 ExceptionHandler
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> httpMediaTypeNotSupportedException(
            final HttpMediaTypeNotSupportedException e
    ) {
        return convert(GlobalErrorCode.UNSUPPORTED_MEDIA_TYPE_ERROR, e);
    }

    /**
     * 내부 서버 오류 전용 ExceptionHandler (With Slack Logging)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAnyException(
            final RuntimeException e,
            final HttpServletRequest request
    ) {
        sendSlackAlertErrorLog(e, request);
        return convert(GlobalErrorCode.INTERNAL_SERVER_ERROR, e);
    }

    /**
     * Exception (With Slack Logging)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAnonymousException(
            final Exception e,
            final HttpServletRequest request
    ) {
        sendSlackAlertErrorLog(e, request);

        final ErrorCode code = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        logging(code, e.getMessage(), e);

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    private ResponseEntity<ErrorResponse> convert(
            final ErrorCode code,
            final Exception e
    ) {
        logging(code, e);
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    private ResponseEntity<ErrorResponse> convert(
            final ErrorCode code,
            final String message,
            final Exception e
    ) {
        logging(code, message, e);
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.of(code, message));
    }

    private void logging(
            final ErrorCode code,
            final Exception e
    ) {
        log.warn(
                "{} | {} | {}",
                code.getStatus(),
                code.getErrorCode(),
                code.getMessage(),
                e
        );
    }

    private void logging(final ErrorCode code, final String message, final Exception e) {
        log.warn(
                "{} | {} | {}",
                code.getStatus(),
                code.getErrorCode(),
                message,
                e
        );
    }

    public void sendSlackAlertErrorLog(final Exception e, final HttpServletRequest request) {
        try {
            SLACK_CLIENT.send(
                    slackWebhookUrl,
                    payload(p -> p
                            .text("서버 에러 발생!!")
                            .attachments(
                                    List.of(generateSlackErrorAttachments(e, request))
                            )
                    ));
        } catch (final IOException slackApiError) {
            log.error("Slack API 통신 간 에러 발생", slackApiError);
        }
    }

    private Attachment generateSlackErrorAttachments(
            final Exception e,
            final HttpServletRequest request
    ) {
        final String requestTime = DateTimeFormatter.ofPattern(DATETIME_FORMAT).format(LocalDateTime.now());
        final String xffHeader = request.getHeader(XFF_HEADER);
        return Attachment.builder()
                .color(LOG_COLOR)
                .title(requestTime + " 발생 에러 로그")
                .fields(
                        List.of(
                                generateSlackField(TITLE_REQUEST_IP, (xffHeader == null) ? request.getRemoteAddr() : xffHeader),
                                generateSlackField(TITLE_REQUEST_URL, createRequestFullPath(request)),
                                generateSlackField(TITLE_ERROR_MESSAGE, e.toString())
                        )
                )
                .build();
    }

    private Field generateSlackField(
            final String title,
            final String value
    ) {
        return Field.builder()
                .title(title)
                .value(value)
                .valueShortEnough(false)
                .build();
    }

    private String createRequestFullPath(final HttpServletRequest request) {
        String fullPath = request.getMethod() + " " + request.getRequestURL();

        final String queryString = request.getQueryString();
        if (queryString != null) {
            fullPath += "?" + queryString;
        }

        return fullPath;
    }
}
