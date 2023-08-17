package com.kgu.studywithme.favorite.application;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.favorite.application.adapter.FavoriteJudgeRepositoryAdapter;
import com.kgu.studywithme.favorite.application.service.StudyLikeCancellationService;
import com.kgu.studywithme.favorite.application.service.StudyLikeMarkingService;
import com.kgu.studywithme.favorite.application.usecase.command.StudyLikeCancellationUseCase;
import com.kgu.studywithme.favorite.application.usecase.command.StudyLikeMarkingUseCase;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.favorite.exception.FavoriteErrorCode;
import com.kgu.studywithme.favorite.infrastructure.persistence.FavoriteJpaRepository;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Favorite -> StudyLikeMarkingService, StudyLikeCancellationService 테스트")
class FavoriteManagerTest extends UseCaseTest {
    @InjectMocks
    private StudyLikeMarkingService studyLikeMarkingService;

    @InjectMocks
    private StudyLikeCancellationService studyLikeCancellationService;

    @Mock
    private FavoriteJudgeRepositoryAdapter favoriteJudgeRepositoryAdapter;

    @Mock
    private FavoriteJpaRepository favoriteJpaRepository;

    private static final Long STUDY_ID = 1L;
    private static final Long MEMBER_ID = 1L;

    @Nested
    @DisplayName("찜 등록")
    class LikeMarking {
        private final StudyLikeMarkingUseCase.Command command = new StudyLikeMarkingUseCase.Command(STUDY_ID, MEMBER_ID);

        @Test
        @DisplayName("이미 찜 등록된 스터디를 찜할 수 없다")
        void throwExceptionByAlreadyLikeMarked() {
            // given
            given(favoriteJudgeRepositoryAdapter.alreadyLikeMarked(any(), any())).willReturn(true);

            // when - then
            assertThatThrownBy(() -> studyLikeMarkingService.invoke(command))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(FavoriteErrorCode.ALREADY_LIKE_MARKED.getMessage());

            assertAll(
                    () -> verify(favoriteJudgeRepositoryAdapter, times(1)).alreadyLikeMarked(any(), any()),
                    () -> verify(favoriteJpaRepository, times(0)).save(any())
            );
        }

        @Test
        @DisplayName("해당 스터디를 찜 등록한다")
        void success() {
            // given
            given(favoriteJudgeRepositoryAdapter.alreadyLikeMarked(any(), any())).willReturn(false);

            final Favorite favorite = Favorite.favoriteMarking(STUDY_ID, MEMBER_ID).apply(1L, LocalDateTime.now());
            given(favoriteJpaRepository.save(any())).willReturn(favorite);

            // when
            final Long savedFavoriteId = studyLikeMarkingService.invoke(command);

            // then
            assertAll(
                    () -> verify(favoriteJudgeRepositoryAdapter, times(1)).alreadyLikeMarked(any(), any()),
                    () -> verify(favoriteJpaRepository, times(1)).save(any()),
                    () -> assertThat(savedFavoriteId).isEqualTo(favorite.getId())
            );
        }
    }

    @Nested
    @DisplayName("찜 취소")
    class LikeCancellation {
        private final StudyLikeCancellationUseCase.Command command = new StudyLikeCancellationUseCase.Command(STUDY_ID, MEMBER_ID);

        @Test
        @DisplayName("찜 등록이 되지 않은 스터디를 취소할 수 없다")
        void throwExceptionByNeverLikeMarked() {
            // given
            given(favoriteJudgeRepositoryAdapter.neverLikeMarked(any(), any())).willReturn(true);

            // when - then
            assertThatThrownBy(() -> studyLikeCancellationService.invoke(command))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(FavoriteErrorCode.NEVER_LIKE_MARKED.getMessage());

            assertAll(
                    () -> verify(favoriteJudgeRepositoryAdapter, times(1)).neverLikeMarked(any(), any()),
                    () -> verify(favoriteJpaRepository, times(0)).cancelLikeMarking(any(), any())
            );
        }

        @Test
        @DisplayName("해당 스터디에 대해서 등록한 찜을 취소한다")
        void success() {
            // given
            given(favoriteJudgeRepositoryAdapter.neverLikeMarked(STUDY_ID, MEMBER_ID)).willReturn(false);

            // when
            studyLikeCancellationService.invoke(command);

            // then
            assertAll(
                    () -> verify(favoriteJudgeRepositoryAdapter, times(1)).neverLikeMarked(any(), any()),
                    () -> verify(favoriteJpaRepository, times(1)).cancelLikeMarking(any(), any())
            );
        }
    }
}
