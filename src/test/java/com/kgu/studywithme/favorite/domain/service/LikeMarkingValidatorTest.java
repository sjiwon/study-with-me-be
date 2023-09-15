package com.kgu.studywithme.favorite.domain.service;

import com.kgu.studywithme.common.ExecuteParallel;
import com.kgu.studywithme.favorite.domain.repository.FavoriteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExecuteParallel
@DisplayName("Favorite -> LikeMarkingValidator 테스트")
public class LikeMarkingValidatorTest {
    private final FavoriteRepository favoriteRepository = mock(FavoriteRepository.class);
    private final LikeMarkingValidator sut = new LikeMarkingValidator(favoriteRepository);

    private static final Long MEMBER_ID = 1L;
    private static final Long STUDY_A_ID = 1L;
    private static final Long STUDY_B_ID = 2L;

    @Test
    @DisplayName("이미 찜했는지 검사한다")
    void alreadyLikeMarked() {
        // given
        given(favoriteRepository.existsByMemberIdAndStudyId(MEMBER_ID, STUDY_A_ID)).willReturn(true);
        given(favoriteRepository.existsByMemberIdAndStudyId(MEMBER_ID, STUDY_B_ID)).willReturn(false);

        // when
        final boolean actual1 = sut.alreadyLikeMarked(MEMBER_ID, STUDY_A_ID);
        final boolean actual2 = sut.alreadyLikeMarked(MEMBER_ID, STUDY_B_ID);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("찜한 적 없는지 검사한다")
    void neverLikeMarked() {
        // given
        given(favoriteRepository.existsByMemberIdAndStudyId(MEMBER_ID, STUDY_A_ID)).willReturn(true);
        given(favoriteRepository.existsByMemberIdAndStudyId(MEMBER_ID, STUDY_B_ID)).willReturn(false);

        // when
        final boolean actual1 = sut.neverLikeMarked(MEMBER_ID, STUDY_A_ID);
        final boolean actual2 = sut.neverLikeMarked(MEMBER_ID, STUDY_B_ID);

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue()
        );
    }
}
