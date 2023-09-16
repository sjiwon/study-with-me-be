package com.kgu.studywithme.file.presentation;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.GlobalErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.constraint;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentRequest;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentResponse;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getExceptionResponseFields;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getHeaderWithAccessToken;
import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("File -> FileUploadApiController 테스트")
class FileUploadApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("Weekly 설명 내부 이미지 업로드 API [POST /api/image] - AccessToken 필수")
    class UploadWeeklyImage {
        private static final String BASE_URL = "/api/image";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("허용하는 이미지 확장자[jpg, jpeg, png, gif]가 아니면 업로드가 불가능하다")
        void throwExceptionByNotAllowedExtension() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);

            // when
            final MultipartFile file = createSingleMockMultipartFile("hello5.webp", "image/webp");
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .queryParam("type", "studyWeeklyContentImage");

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.VALIDATION_ERROR;
            final String message = "이미지는 jpg, jpeg, png, gif만 허용합니다.";
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(message)
                    )
                    .andDo(
                            document(
                                    "UploadApi/Image/Weekly/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    requestParts(
                                            partWithName("file")
                                                    .description("스터디 Weekly 설명에 포함되는 이미지")
                                    ),
                                    queryParameters(
                                            parameterWithName("type")
                                                    .description("이미지 업로드 타입")
                                                    .attributes(constraint("스터디 설명 이미지 = studyDescriptionImage / Weekly 설명 이미지 = studyWeeklyContentImage"))
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 Weekly 설명에 포함되는 이미지를 업로드한다")
        void success() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);

            final String uploadLink = "https://image-upload-link";
            given(uploadImageUseCase.invoke(any())).willReturn(uploadLink);

            // when
            final MultipartFile file = createSingleMockMultipartFile("hello4.png", "image/png");
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .queryParam("type", "studyWeeklyContentImage");

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result").exists(),
                            jsonPath("$.result").value(uploadLink)
                    )
                    .andDo(
                            document(
                                    "UploadApi/Image/Weekly/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    requestParts(
                                            partWithName("file")
                                                    .description("스터디 Weekly 설명에 포함되는 이미지")
                                    ),
                                    queryParameters(
                                            parameterWithName("type")
                                                    .description("이미지 업로드 타입")
                                                    .attributes(constraint("스터디 설명 이미지 = studyDescriptionImage / Weekly 설명 이미지 = studyWeeklyContentImage"))
                                    ),
                                    responseFields(
                                            fieldWithPath("result")
                                                    .description("업로드된 이미지 링크")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("스터디 설명 내부 이미지 업로드 API [POST /api/image] - AccessToken 필수")
    class UploadStudyDescriptionImage {
        private static final String BASE_URL = "/api/image";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("허용하는 이미지 확장자[jpg, jpeg, png, gif]가 아니면 업로드가 불가능하다")
        void throwExceptionByNotAllowedExtension() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);

            // when
            final MultipartFile file = createSingleMockMultipartFile("hello5.webp", "image/webp");
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .queryParam("type", "studyDescriptionImage");

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.VALIDATION_ERROR;
            final String message = "이미지는 jpg, jpeg, png, gif만 허용합니다.";
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(message)
                    )
                    .andDo(
                            document(
                                    "UploadApi/Image/Description/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    requestParts(
                                            partWithName("file")
                                                    .description("스터디 설명에 포함되는 이미지")
                                    ),
                                    queryParameters(
                                            parameterWithName("type")
                                                    .description("이미지 업로드 타입")
                                                    .attributes(constraint("스터디 설명 이미지 = studyDescriptionImage / Weekly 설명 이미지 = studyWeeklyContentImage"))
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("스터디 설명에 포함되는 이미지를 업로드한다")
        void success() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);

            final String uploadLink = "https://image-upload-link";
            given(uploadImageUseCase.invoke(any())).willReturn(uploadLink);

            // when
            final MultipartFile file = createSingleMockMultipartFile("hello4.png", "image/png");
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .queryParam("type", "studyDescriptionImage");

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result").exists(),
                            jsonPath("$.result").value(uploadLink)
                    )
                    .andDo(
                            document(
                                    "UploadApi/Image/Description/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    requestParts(
                                            partWithName("file")
                                                    .description("스터디 설명에 포함되는 이미지")
                                    ),
                                    queryParameters(
                                            parameterWithName("type")
                                                    .description("이미지 업로드 타입")
                                                    .attributes(constraint("스터디 설명 이미지 = studyDescriptionImage / Weekly 설명 이미지 = studyWeeklyContentImage"))
                                    ),
                                    responseFields(
                                            fieldWithPath("result")
                                                    .description("업로드된 이미지 링크")
                                    )
                            )
                    );
        }
    }
}
