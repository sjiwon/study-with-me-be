package com.kgu.studywithme.global.interceptor;

import com.kgu.studywithme.global.logging.LoggingStatus;
import com.kgu.studywithme.global.logging.LoggingStatusManager;
import com.kgu.studywithme.global.logging.RequestMetaData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

import static com.kgu.studywithme.global.logging.RequestMetadataExtractor.getClientIP;
import static com.kgu.studywithme.global.logging.RequestMetadataExtractor.getHttpMethod;
import static com.kgu.studywithme.global.logging.RequestMetadataExtractor.getRequestUriWithQueryString;

@Slf4j
@RequiredArgsConstructor
public class RequestLogInterceptor implements HandlerInterceptor {
    private static final String[] INFRA_URI = {"/favicon.ico", "/error*", "/swagger*", "/actuator*"};

    private final LoggingStatusManager loggingStatusManager;

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) {
        if (CorsUtils.isPreFlightRequest(request) || isInfraUri(request)) {
            return true;
        }

        final LoggingStatus loggingStatus = new LoggingStatus(generateRandomUuid());
        loggingStatusManager.applyLoggingStatus(loggingStatus);

        final RequestMetaData requestMetaData = new RequestMetaData(loggingStatus.getTaskId(), request);
        log.info("[Request START] -> [{}]", requestMetaData);
        return true;
    }

    @Override
    public void afterCompletion(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler,
            final Exception ex
    ) {
        if (CorsUtils.isPreFlightRequest(request) || isInfraUri(request)) {
            return;
        }

        final LoggingStatus loggingStatus = loggingStatusManager.get();
        final long totalTime = loggingStatus.totalTakenTime();
        log.info("[Request END] -> [Task ID = {}, IP = {}, HTTP Method = {}, Uri = {}, HTTP Status = {}, 요청 처리 시간 = {}ms]",
                loggingStatus.getTaskId(),
                getClientIP(request),
                getHttpMethod(request),
                getRequestUriWithQueryString(request),
                response.getStatus(),
                totalTime
        );
    }

    private boolean isInfraUri(final HttpServletRequest request) {
        return PatternMatchUtils.simpleMatch(INFRA_URI, request.getRequestURI());
    }

    private String generateRandomUuid() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
