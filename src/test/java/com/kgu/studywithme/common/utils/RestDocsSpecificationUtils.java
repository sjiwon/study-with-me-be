package com.kgu.studywithme.common.utils;

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.restdocs.snippet.Snippet;

import static com.kgu.studywithme.auth.utils.TokenResponseWriter.REFRESH_TOKEN_COOKIE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

public class RestDocsSpecificationUtils {
    public static OperationRequestPreprocessor getDocumentRequest() {
        return preprocessRequest(prettyPrint());
    }

    public static OperationResponsePreprocessor getDocumentResponse() {
        return preprocessResponse(prettyPrint());
    }

    public static Snippet getHeaderWithAccessToken() {
        return requestHeaders(
                headerWithName(AUTHORIZATION).description("Access Token")
        );
    }

    public static Snippet getHeaderWithRefreshToken() {
        return requestCookies(
                cookieWithName(REFRESH_TOKEN_COOKIE).description("Refresh Token")
        );
    }

    public static Snippet getExceptionResponseFields() {
        return responseFields(
                fieldWithPath("status").description("HTTP 상태 코드"),
                fieldWithPath("errorCode").description("커스텀 예외 코드"),
                fieldWithPath("message").description("예외 메시지")
        );
    }

    public static Attributes.Attribute constraint(final String value) {
        return new Attributes.Attribute("constraints", value);
    }
}
