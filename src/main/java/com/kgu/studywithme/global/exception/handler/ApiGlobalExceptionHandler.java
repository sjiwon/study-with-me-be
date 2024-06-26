package com.kgu.studywithme.global.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgu.studywithme.auth.domain.model.oauth.OAuthUserResponse;
import com.kgu.studywithme.global.exception.ErrorCode;
import com.kgu.studywithme.global.exception.GlobalErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.global.exception.StudyWithMeOAuthException;
import com.kgu.studywithme.global.exception.dto.ErrorResponse;
import com.kgu.studywithme.global.exception.dto.OAuthExceptionResponse;
import com.kgu.studywithme.global.exception.slack.SlackAlertManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kgu.studywithme.global.log.RequestMetadataExtractor.getRequestUriWithQueryString;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ApiGlobalExceptionHandler {
    private final ObjectMapper objectMapper;
    private final SlackAlertManager slackAlertManager;

    @ExceptionHandler(StudyWithMeException.class)
    public ResponseEntity<ErrorResponse> studyWithMeException(final StudyWithMeException e) {
        final ErrorCode code = e.getCode();
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
        log.warn("HttpMessageNotReadableException Occurred", e);

        final ErrorCode code = GlobalErrorCode.VALIDATION_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    /**
     * 요청 파라미터 Validation 전용 ExceptionHandler
     */
    @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> unsatisfiedServletRequestParameterException(final UnsatisfiedServletRequestParameterException e) {
        log.warn("UnsatisfiedServletRequestParameterException Occurred", e);

        final ErrorCode code = GlobalErrorCode.VALIDATION_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    /**
     * 요청 데이터 Validation 전용 ExceptionHandler (@RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException Occurred", e);

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
    public ResponseEntity<ErrorResponse> bindException(final BindException e) {
        log.warn("BindException Occurred", e);

        final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        final ErrorCode code = GlobalErrorCode.VALIDATION_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.of(code, extractErrorMessage(fieldErrors)));
    }

    private String extractErrorMessage(final List<FieldError> fieldErrors) {
        if (fieldErrors.size() == 1) {
            return fieldErrors.get(0).getDefaultMessage();
        }

        final Map<String, String> errors = new HashMap<>();
        for (FieldError error : fieldErrors) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        try {
            return objectMapper.writeValueAsString(errors);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException("JSON Parsing Error...", e);
        }
    }

    /**
     * 존재하지 않는 Endpoint 전용 ExceptionHandler
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> noHandlerFoundException(final NoHandlerFoundException e) {
        log.warn("NoHandlerFoundException Occurred", e);

        final ErrorCode code = GlobalErrorCode.NOT_SUPPORTED_URI_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    /**
     * Method Argument Exception 전용 ExceptionHandler
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> methodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        log.warn("MethodArgumentTypeMismatchException Occurred", e);

        final ErrorCode code = GlobalErrorCode.NOT_SUPPORTED_URI_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    /**
     * HTTP Request Method 오류 전용 ExceptionHandler
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> httpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        log.warn("HttpRequestMethodNotSupportedException Occurred", e);

        final ErrorCode code = GlobalErrorCode.NOT_SUPPORTED_METHOD_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    /**
     * MediaType 전용 ExceptionHandler
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> httpMediaTypeNotSupportedException(final HttpMediaTypeNotSupportedException e) {
        log.warn("HttpMediaTypeNotSupportedException Occurred", e);

        final ErrorCode code = GlobalErrorCode.UNSUPPORTED_MEDIA_TYPE_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    /**
     * 내부 서버 오류 전용 ExceptionHandler (With Slack Alert)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAnyException(final HttpServletRequest request, final RuntimeException e) {
        log.error(
                "RuntimeException Occurred -> {} {}",
                request.getMethod(),
                getRequestUriWithQueryString(request),
                e
        );
        slackAlertManager.sendErrorLog(request, e);

        final ErrorCode code = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    /**
     * Exception (With Slack Alert)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAnonymousException(final HttpServletRequest request, final Exception e) {
        log.error(
                "Unknown Exception Occurred -> {} {}",
                request.getMethod(),
                getRequestUriWithQueryString(request),
                e
        );
        slackAlertManager.sendErrorLog(request, e);

        final ErrorCode code = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }
}
