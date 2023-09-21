package com.kgu.studywithme.member.presentation;

import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import com.kgu.studywithme.member.presentation.dto.request.SignUpMemberRequest;
import com.kgu.studywithme.member.presentation.dto.request.UpdateMemberRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.stream.Collectors;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.constraint;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentRequest;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentResponse;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getExceptionResponseFields;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getHeaderWithAccessToken;
import static com.kgu.studywithme.common.utils.TokenUtils.applyAccessTokenToAuthorizationHeader;
import static com.kgu.studywithme.member.domain.model.Gender.MALE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member -> MemberApiController 테스트")
class MemberApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("회원가입 API [POST /api/members]")
    class SignUp {
        private static final String BASE_URL = "/api/members";
        private static final SignUpMemberRequest REQUEST = new SignUpMemberRequest(
                JIWON.getName(),
                JIWON.getNickname().getValue(),
                JIWON.getEmail().getValue(),
                JIWON.getBirth(),
                JIWON.getPhone().getValue(),
                MALE.getSimpleValue(),
                JIWON.getAddress().getProvince(),
                JIWON.getAddress().getCity(),
                JIWON.getEmail().isEmailOptIn(),
                JIWON.getInterests()
                        .stream()
                        .map(Category::getId)
                        .collect(Collectors.toSet())
        );

        @Test
        @DisplayName("중복되는 값(닉네임)에 의해서 회원가입에 실패한다 [Case: 닉네임, 이메일, 전화번호]")
        void throwExceptionByDuplicateNickname() throws Exception {
            // given
            doThrow(StudyWithMeException.type(MemberErrorCode.DUPLICATE_NICKNAME))
                    .when(signUpMemberUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(REQUEST));

            // then
            final MemberErrorCode expectedError = MemberErrorCode.DUPLICATE_NICKNAME;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isConflict())
                    .andExpectAll(getResultMatchersViaErrorCode(expectedError))
                    .andDo(
                            document(
                                    "MemberApi/SignUp/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("name")
                                                    .description("이름")
                                                    .attributes(constraint("서버 제공 [Read-Only]")),
                                            fieldWithPath("nickname")
                                                    .description("닉네임"),
                                            fieldWithPath("email")
                                                    .description("이메일")
                                                    .attributes(constraint("서버 제공 [Read-Only]")),
                                            fieldWithPath("birth")
                                                    .description("생년월일"),
                                            fieldWithPath("phone")
                                                    .description("전화번호"),
                                            fieldWithPath("gender")
                                                    .description("성별")
                                                    .attributes(constraint("남성[M] / 여성[F]")),
                                            fieldWithPath("province")
                                                    .description("거주지 [경기도, 강원도, ...]"),
                                            fieldWithPath("city")
                                                    .description("거주지 [안양시, 수원시, ...]"),
                                            fieldWithPath("emailOptIn")
                                                    .description("이메일 수신 동의 여부"),
                                            fieldWithPath("interests[]")
                                                    .description("관심사 Enum ID")
                                                    .attributes(constraint("스터디 카테고리 ID 한정"))
                                    ),
                                    getExceptionResponseFields()
                            )
                    );
        }

        @Test
        @DisplayName("회원가입을 진행한다")
        void success() throws Exception {
            // given
            given(signUpMemberUseCase.invoke(any())).willReturn(1L);

            // when
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(REQUEST));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isCreated(),
                            jsonPath("$.memberId").value(1L)
                    )
                    .andDo(
                            document(
                                    "MemberApi/SignUp/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("name")
                                                    .description("이름")
                                                    .attributes(constraint("서버 제공 [Read-Only]")),
                                            fieldWithPath("nickname")
                                                    .description("닉네임"),
                                            fieldWithPath("email")
                                                    .description("이메일")
                                                    .attributes(constraint("서버 제공 [Read-Only]")),
                                            fieldWithPath("birth")
                                                    .description("생년월일"),
                                            fieldWithPath("phone")
                                                    .description("전화번호"),
                                            fieldWithPath("gender")
                                                    .description("성별")
                                                    .attributes(constraint("남성[M] / 여성[F]")),
                                            fieldWithPath("province")
                                                    .description("거주지 [경기도, 강원도, ...]"),
                                            fieldWithPath("city")
                                                    .description("거주지 [안양시, 수원시, ...]"),
                                            fieldWithPath("emailOptIn")
                                                    .description("이메일 수신 동의 여부"),
                                            fieldWithPath("interests[]")
                                                    .description("관심사 Enum ID")
                                                    .attributes(constraint("스터디 카테고리 ID 한정"))
                                    ),
                                    responseFields(
                                            fieldWithPath("memberId")
                                                    .description("생성한 사용자 ID(PK)")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자 정보 수정 [PATCH /api/members/me] - AccessToken 필수")
    class Update {
        private static final String BASE_URL = "/api/members/me";
        private static final Long MEMBER_ID = 1L;
        private static final UpdateMemberRequest REQUEST = new UpdateMemberRequest(
                JIWON.getNickname().getValue(),
                JIWON.getPhone().getValue(),
                JIWON.getAddress().getProvince(),
                JIWON.getAddress().getCity(),
                false,
                JIWON.getInterests()
                        .stream()
                        .map(Category::getId)
                        .collect(Collectors.toSet())
        );

        @Test
        @DisplayName("사용자 정보를 수정한다")
        void success() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);
            doNothing()
                    .when(updateMemberUseCase)
                    .invoke(any());

            // when
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .patch(BASE_URL)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader())
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(REQUEST));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "MemberApi/Update",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    requestFields(
                                            fieldWithPath("nickname")
                                                    .description("닉네임"),
                                            fieldWithPath("phone")
                                                    .description("전화번호"),
                                            fieldWithPath("province")
                                                    .description("거주지 [경기도, 강원도, ...]"),
                                            fieldWithPath("city")
                                                    .description("거주지 [안양시, 수원시, ...]"),
                                            fieldWithPath("emailOptIn")
                                                    .description("이메일 수신 동의 여부"),
                                            fieldWithPath("interests")
                                                    .description("관심사 Enum ID")
                                                    .attributes(constraint("스터디 카테고리 ID 한정"))
                                    )
                            )
                    );
        }
    }
}
