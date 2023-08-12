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
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
import static org.springframework.http.HttpStatus.NOT_FOUND;

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
        log.warn(
                "StudyWithMeException Occurred -> {} | {} | {}",
                code.getStatus(),
                code.getErrorCode(),
                code.getMessage(),
                e
        );

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    @ExceptionHandler(StudyWithMeOAuthException.class)
    public ResponseEntity<OAuthExceptionResponse> studyWithMeOAuthException(final StudyWithMeOAuthException e) {
        final OAuthUserResponse response = e.getResponse();
        return ResponseEntity
                .status(NOT_FOUND)
                .body(new OAuthExceptionResponse(response));
    }

    /**
     * JSON Parsing Error 전용 ExceptionHandler
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> httpMessageNotReadableException(final HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException Occurred -> {}", e.getMessage(), e);

        final ErrorCode code = GlobalErrorCode.VALIDATION_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    /**
     * 요청 파라미터 Validation 전용 ExceptionHandler
     */
    @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> unsatisfiedServletRequestParameterException(
            final UnsatisfiedServletRequestParameterException e
    ) {
        log.warn("UnsatisfiedServletRequestParameterException Occurred -> {}", e.getMessage(), e);

        final ErrorCode code = GlobalErrorCode.VALIDATION_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    /**
     * 요청 데이터 Validation 전용 ExceptionHandler (@RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(
            final MethodArgumentNotValidException e
    ) throws JsonProcessingException {
        log.warn("MethodArgumentNotValidException Occurred -> {}", e.getMessage(), e);

        final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        final ErrorCode code = GlobalErrorCode.VALIDATION_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.of(code, extractErrorMessage(fieldErrors)));
    }

    /**
     * 요청 데이터 Validation 전용 ExceptionHandler (@ModelAttribute)
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> bindException(final BindException e) throws JsonProcessingException {
        log.warn("BindException Occurred -> {}", e.getMessage(), e);

        final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        final ErrorCode code = GlobalErrorCode.VALIDATION_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.of(code, extractErrorMessage(fieldErrors)));
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
        log.warn("NoHandlerFoundException Occurred -> {}", e.getMessage(), e);

        final ErrorCode code = GlobalErrorCode.NOT_SUPPORTED_URI_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    /**
     * Method Argument Exception 전용 ExceptionHandler
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> methodArgumentTypeMismatchException(
            final MethodArgumentTypeMismatchException e
    ) {
        log.warn("MethodArgumentTypeMismatchException Occurred -> {}", e.getMessage(), e);

        final ErrorCode code = GlobalErrorCode.NOT_SUPPORTED_URI_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    /**
     * HTTP Request Method 오류 전용 ExceptionHandler
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> httpRequestMethodNotSupportedException(
            final HttpRequestMethodNotSupportedException e
    ) {
        log.warn("HttpRequestMethodNotSupportedException Occurred -> {}", e.getMessage(), e);

        final ErrorCode code = GlobalErrorCode.NOT_SUPPORTED_METHOD_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    /**
     * MediaType 전용 ExceptionHandler
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> httpMediaTypeNotSupportedException(
            final HttpMediaTypeNotSupportedException e
    ) {
        log.warn("HttpMediaTypeNotSupportedException Occurred -> {}", e.getMessage(), e);

        final ErrorCode code = GlobalErrorCode.UNSUPPORTED_MEDIA_TYPE_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    /**
     * 내부 서버 오류 전용 ExceptionHandler (With Slack Logging)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAnyException(
            final RuntimeException e,
            final HttpServletRequest request
    ) {
        log.error(
                "RuntimeException Occurred -> {} {} | {}",
                request.getMethod(),
                createRequestUriWithQueryString(request),
                e.getMessage(),
                e
        );
        sendSlackAlertErrorLog(e, request);

        final ErrorCode code = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    /**
     * Exception (With Slack Logging)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAnonymousException(
            final Exception e,
            final HttpServletRequest request
    ) {
        log.error(
                "Unknown Exception Occurred -> {} {} | {}",
                request.getMethod(),
                createRequestUriWithQueryString(request),
                e.getMessage(),
                e
        );
        sendSlackAlertErrorLog(e, request);

        final ErrorCode code = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    private String createRequestUriWithQueryString(final HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        final String queryString = request.getQueryString();
        if (queryString != null) {
            requestURI += "?" + queryString;
        }

        return requestURI;
    }

    public void sendSlackAlertErrorLog(
            final Exception e,
            final HttpServletRequest request
    ) {
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
