package com.kgu.studywithme.study.utils.search;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.study.utils.search.SearchSortType.DATE;
import static com.kgu.studywithme.study.utils.search.SearchSortType.FAVORITE;
import static com.kgu.studywithme.study.utils.search.SearchSortType.REVIEW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study -> SearchSortType 테스트")
public class SearchSortTypeTest extends ParallelTest {
    @Test
    @DisplayName("스터디 검색 조건 SearchSortType을 조회한다")
    void getSearchSortType() {
        assertAll(
                () -> assertThat(SearchSortType.from("date")).isEqualTo(DATE),
                () -> assertThat(SearchSortType.from("favorite")).isEqualTo(FAVORITE),
                () -> assertThat(SearchSortType.from("review")).isEqualTo(REVIEW)
        );
    }
    
    @Test
    @DisplayName("제공하지 않는 SearchSortType은 조회할 수 없다")
    void throwExceptionByInvalidSortType() {
        assertThatThrownBy(() -> SearchSortType.from("anonymous"))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.INVALID_SORT_TYPE.getMessage());
    }
}
