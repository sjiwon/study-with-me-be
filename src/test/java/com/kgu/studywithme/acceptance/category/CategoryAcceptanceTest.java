package com.kgu.studywithme.acceptance.category;

import com.kgu.studywithme.common.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.acceptance.category.CategoryAcceptanceFixture.모든_스터디_카테고리를_조회한다;
import static org.springframework.http.HttpStatus.OK;

@DisplayName("[Acceptance Test] 카테고리 관련 기능")
public class CategoryAcceptanceTest extends AcceptanceTest {
    @Test
    @DisplayName("모든 스터디 카테고리를 조회한다")
    void findAllCategoriesApi() {
        모든_스터디_카테고리를_조회한다()
                .statusCode(OK.value());
    }
}
