package com.kgu.studywithme.upload.presentation;

import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.GlobalErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.upload.exception.UploadErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.*;
import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Upload -> UploadApiController 테스트")
class UploadApiControllerTest extends ControllerTest {
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
                    .queryParam("type", "weekly");

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
                                    "UploadApi/Image/Weekly/Failure/Case1",
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
                                                    .attributes(constraint("주차별 이미지 = weekly / 스터디 설명 이미지 = description"))
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("이미지를 전송하지 않거나 크기가 0인 이미지면 업로드를 실패한다")
        void throwExceptionByFileIsEmpty() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);
            doThrow(StudyWithMeException.type(UploadErrorCode.FILE_IS_EMPTY))
                    .when(uploadWeeklyImageUseCase)
                    .uploadWeeklyImage(any());

            // when
            final MultipartFile file = new MockMultipartFile("file", new byte[0]);
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .queryParam("type", "weekly");

            // then
            final UploadErrorCode expectedError = UploadErrorCode.FILE_IS_EMPTY;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "UploadApi/Image/Weekly/Failure/Case2",
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
                                                    .attributes(constraint("주차별 이미지 = weekly / 스터디 설명 이미지 = description"))
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
            given(uploadWeeklyImageUseCase.uploadWeeklyImage(any())).willReturn(uploadLink);

            // when
            final MultipartFile file = createSingleMockMultipartFile("hello4.png", "image/png");
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .queryParam("type", "weekly");

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
                                                    .attributes(constraint("주차별 이미지 = weekly / 스터디 설명 이미지 = description"))
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
                    .queryParam("type", "description");

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
                                    "UploadApi/Image/Description/Failure/Case1",
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
                                                    .attributes(constraint("주차별 이미지 = weekly / 스터디 설명 이미지 = description"))
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("이미지를 전송하지 않거나 크기가 0인 이미지면 업로드를 실패한다")
        void throwExceptionByFileIsEmpty() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);
            doThrow(StudyWithMeException.type(UploadErrorCode.FILE_IS_EMPTY))
                    .when(uploadStudyDescriptionImageUseCase)
                    .uploadStudyDescriptionImage(any());

            // when
            final MultipartFile file = new MockMultipartFile("file", new byte[0]);
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .queryParam("type", "description");

            // then
            final UploadErrorCode expectedError = UploadErrorCode.FILE_IS_EMPTY;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "UploadApi/Image/Description/Failure/Case2",
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
                                                    .attributes(constraint("주차별 이미지 = weekly / 스터디 설명 이미지 = description"))
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
            given(uploadStudyDescriptionImageUseCase.uploadStudyDescriptionImage(any())).willReturn(uploadLink);

            // when
            final MultipartFile file = createSingleMockMultipartFile("hello4.png", "image/png");
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL)
                    .file((MockMultipartFile) file)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .queryParam("type", "description");

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
                                                    .attributes(constraint("주차별 이미지 = weekly / 스터디 설명 이미지 = description"))
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
