package com.kgu.studywithme.category.presentation;

import com.kgu.studywithme.category.domain.model.CategoryResponse;
import com.kgu.studywithme.common.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.kgu.studywithme.category.domain.model.Category.APTITUDE_NCS;
import static com.kgu.studywithme.category.domain.model.Category.CERTIFICATION;
import static com.kgu.studywithme.category.domain.model.Category.ETC;
import static com.kgu.studywithme.category.domain.model.Category.INTERVIEW;
import static com.kgu.studywithme.category.domain.model.Category.LANGUAGE;
import static com.kgu.studywithme.category.domain.model.Category.PROGRAMMING;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentRequest;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentResponse;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Category -> CategoryApiController 테스트")
class CategoryApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("스터디 카테고리 조회 API [GET /api/categories]")
    class GetAllCategories {
        private static final String BASE_URL = "/api/categories";

        @Test
        @DisplayName("전체 스터디 카테고리를 조회한다")
        void success() throws Exception {
            // given
            final List<CategoryResponse> categories = List.of(
                    new CategoryResponse(LANGUAGE),
                    new CategoryResponse(INTERVIEW),
                    new CategoryResponse(PROGRAMMING),
                    new CategoryResponse(APTITUDE_NCS),
                    new CategoryResponse(CERTIFICATION),
                    new CategoryResponse(ETC)
            );
            given(getAllCategoriesUseCase.invoke()).willReturn(categories);

            // when
            final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result").exists(),
                            jsonPath("$.result.size()").value(categories.size())
                    )
                    .andDo(
                            document(
                                    "CategoryApi/FindAll",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    responseFields(
                                            fieldWithPath("result[].id")
                                                    .description("카테고리 ID(PK)"),
                                            fieldWithPath("result[].name")
                                                    .description("카테고리명")
                                    )
                            )
                    );
        }
    }
}
