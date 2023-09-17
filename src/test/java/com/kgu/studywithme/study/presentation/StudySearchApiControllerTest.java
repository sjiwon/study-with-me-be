package com.kgu.studywithme.study.presentation;

import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.common.ControllerTest;
import com.kgu.studywithme.study.application.usecase.dto.StudyPagingResponse;
import com.kgu.studywithme.study.domain.repository.query.dto.StudyPreview;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.kgu.studywithme.category.domain.model.Category.PROGRAMMING;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.constraint;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentRequest;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getDocumentResponse;
import static com.kgu.studywithme.common.utils.RestDocsSpecificationUtils.getHeaderWithAccessToken;
import static com.kgu.studywithme.common.utils.TokenUtils.applyAccessTokenToAuthorizationHeader;
import static com.kgu.studywithme.study.domain.model.RecruitmentStatus.IN_PROGRESS;
import static com.kgu.studywithme.study.domain.model.StudyType.ONLINE;
import static com.kgu.studywithme.study.domain.model.paging.PagingConstants.SLICE_PER_PAGE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Study -> StudySearchApiController 테스트")
class StudySearchApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("각 카테고리별 스터디 조회 API [GET /api/studies]")
    class QueryStudyByCategory {
        private static final String BASE_URL = "/api/studies";

        @Test
        @DisplayName("카테고리로 스터디 리스트를 조회한다 [Ex) 프로그래밍]")
        void success() throws Exception {
            // given
            given(studySearchUseCase.getStudiesByCategory(any())).willReturn(new StudyPagingResponse(generateStudies(), true));
            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("category", String.valueOf(PROGRAMMING.getId()))
                    .param("sort", "date")
                    .param("page", String.valueOf(0))
                    .param("type", "online");

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "StudyApi/Search/Category",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    queryParameters(
                                            parameterWithName("category")
                                                    .description("카테고리 ID"),
                                            parameterWithName("sort")
                                                    .description("정렬 기준")
                                                    .attributes(constraint("date=최신순 / favorite=찜 / review=리뷰")),
                                            parameterWithName("page")
                                                    .description("현재 페이지")
                                                    .attributes(constraint("시작 페이지 = 0")),
                                            parameterWithName("type")
                                                    .description("온라인/오프라인 유무")
                                                    .optional()
                                                    .attributes(constraint("null(온 + 오프) / online / offline")),
                                            parameterWithName("province")
                                                    .description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("type이 오프라인일 경우 활성화")),
                                            parameterWithName("city")
                                                    .description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("type이 오프라인일 경우 활성화"))
                                    ),
                                    responseFields(
                                            fieldWithPath("studies[].id")
                                                    .description("스터디 ID(PK)"),
                                            fieldWithPath("studies[].name")
                                                    .description("스터디명"),
                                            fieldWithPath("studies[].description")
                                                    .description("스터디 설명"),
                                            fieldWithPath("studies[].category")
                                                    .description("스터디 카테고리"),
                                            fieldWithPath("studies[].thumbnail.name")
                                                    .description("스터디 썸네일 이미지"),
                                            fieldWithPath("studies[].thumbnail.background")
                                                    .description("스터디 썸네일 배경색"),
                                            fieldWithPath("studies[].type")
                                                    .description("스터디 타입")
                                                    .attributes(constraint("온라인 / 오프라인")),
                                            fieldWithPath("studies[].recruitmentStatus")
                                                    .description("스터디 모집 여부"),
                                            fieldWithPath("studies[].maxMember")
                                                    .description("스터디 최대 인원"),
                                            fieldWithPath("studies[].participantMembers")
                                                    .description("현재 스터디 참여자 수"),
                                            fieldWithPath("studies[].creationDate")
                                                    .description("스터디 생성 날짜"),
                                            fieldWithPath("studies[].hashtags[]")
                                                    .description("스터디 해시태그"),
                                            fieldWithPath("studies[].likeMarkingMembers[]")
                                                    .description("스터디 찜 사용자 ID(PK) 리스트"),
                                            fieldWithPath("hasNext")
                                                    .description("다음 스크롤 존재 여부")
                                                    .attributes(constraint("false면 무한 스크롤 종료"))
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자의 관심사에 따른 스터디 조회 API [GET /api/studies/recommend] - AccessToken 필수")
    class QueryStudyByRecommend {
        private static final String BASE_URL = "/api/studies/recommend";
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("사용자의 관심사에 따른 스터디 리스트를 조회한다 [언어 / 면접 / 프로그래밍]")
        void success() throws Exception {
            // given
            mockingToken(true, MEMBER_ID);
            given(studySearchUseCase.getStudiesByRecommend(any())).willReturn(new StudyPagingResponse(generateStudies(), true));

            // when
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, applyAccessTokenToAuthorizationHeader())
                    .param("sort", "date")
                    .param("page", String.valueOf(0))
                    .param("type", "online");

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "StudyApi/Search/Recommend",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    queryParameters(
                                            parameterWithName("sort")
                                                    .description("정렬 기준")
                                                    .attributes(constraint("date=최신순 / favorite=찜 / review=리뷰")),
                                            parameterWithName("page")
                                                    .description("현재 페이지")
                                                    .attributes(constraint("시작 페이지 = 0")),
                                            parameterWithName("type")
                                                    .description("온라인/오프라인 유무")
                                                    .optional()
                                                    .attributes(constraint("null(온 + 오프) / online / offline")),
                                            parameterWithName("province")
                                                    .description("오프라인 스터디 지역 [경기도, 강원도, ...]")
                                                    .optional()
                                                    .attributes(constraint("type이 오프라인일 경우 활성화")),
                                            parameterWithName("city")
                                                    .description("오프라인 스터디 지역 [안양시, 수원시, ...]")
                                                    .optional()
                                                    .attributes(constraint("type이 오프라인일 경우 활성화"))
                                    ),
                                    responseFields(
                                            fieldWithPath("studies[].id")
                                                    .description("스터디 ID(PK)"),
                                            fieldWithPath("studies[].name")
                                                    .description("스터디명"),
                                            fieldWithPath("studies[].description")
                                                    .description("스터디 설명"),
                                            fieldWithPath("studies[].category")
                                                    .description("스터디 카테고리"),
                                            fieldWithPath("studies[].thumbnail.name")
                                                    .description("스터디 썸네일"),
                                            fieldWithPath("studies[].thumbnail.background")
                                                    .description("스터디 썸네일 배경색"),
                                            fieldWithPath("studies[].type")
                                                    .description("스터디 타입")
                                                    .attributes(constraint("온라인 / 오프라인")),
                                            fieldWithPath("studies[].recruitmentStatus")
                                                    .description("스터디 모집 여부"),
                                            fieldWithPath("studies[].maxMember")
                                                    .description("스터디 최대 인원"),
                                            fieldWithPath("studies[].participantMembers")
                                                    .description("현재 스터디 참여자 수"),
                                            fieldWithPath("studies[].creationDate")
                                                    .description("스터디 생성 날짜"),
                                            fieldWithPath("studies[].hashtags[]")
                                                    .description("스터디 해시태그"),
                                            fieldWithPath("studies[].likeMarkingMembers[]")
                                                    .description("스터디 찜 사용자 ID(PK) 리스트"),
                                            fieldWithPath("hasNext")
                                                    .description("다음 스크롤 존재 여부")
                                                    .attributes(constraint("false면 무한 스크롤 종료"))
                                    )
                            )
                    );
        }
    }

    private List<StudyPreview> generateStudies() {
        final List<StudyPreview> result = new ArrayList<>();
        final LocalDateTime now = LocalDateTime.now();

        for (long index = 1; index <= SLICE_PER_PAGE; index++) {
            result.add(new StudyPreview(
                    index,
                    "Study" + index,
                    "Hello Study" + index,
                    Category.from((long) (Math.random() * 6 + 1)).getName(),
                    new StudyPreview.Thumbnail("스터디 썸네일.png", "스터디 썸네일 백그라운드 RGB"),
                    ONLINE,
                    IN_PROGRESS,
                    10,
                    8,
                    now.minusDays(index),
                    List.of("해시태그A", "해시태그B", "해시태그C"),
                    generateLikeMarkingMembers()
            ));
        }

        return result;
    }

    private List<Long> generateLikeMarkingMembers() {
        final List<Long> result = new ArrayList<>();
        final int random = (int) (Math.random() * 10);

        for (int i = 0; i < random; i++) {
            result.add((long) (Math.random() * 100 + 1));
        }

        return result;
    }
}
