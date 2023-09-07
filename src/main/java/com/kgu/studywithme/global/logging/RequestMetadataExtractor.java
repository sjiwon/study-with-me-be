package com.kgu.studywithme.global.logging;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Enumeration;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class RequestMetadataExtractor {
    public static String getClientIP(final HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    public static String getHttpMethod(final HttpServletRequest request) {
        return request.getMethod();
    }

    public static String getRequestUriWithQueryString(final HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        final String queryString = request.getQueryString();
        if (queryString != null) {
            requestURI += "?" + queryString;
        }

        return requestURI;
    }

    public static String getSeveralParamsViaParsing(final HttpServletRequest request) {
        final Enumeration<String> parameterNames = request.getParameterNames();
        final Stream<String> parameterStream = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(parameterNames.asIterator(), Spliterator.ORDERED), false
        );
        return parameterStream.map(param -> "%s = %s".formatted(param, request.getParameter(param)))
                .collect(Collectors.joining(", ", "[", "]"));
    }
}