package com.kgu.studywithme.favorite.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.favorite.application.usecase.command.CancelStudyLikeCommand;
import com.kgu.studywithme.favorite.application.usecase.command.MarkStudyLikeCommand;
import com.kgu.studywithme.favorite.domain.model.Favorite;
import com.kgu.studywithme.favorite.domain.repository.FavoriteRepository;
import com.kgu.studywithme.favorite.domain.service.LikeMarkingValidator;
import com.kgu.studywithme.favorite.exception.FavoriteErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Favorite -> MarkStudyLikeUseCase, CancelStudyLikeUseCase 테스트")
class StudyLikeMarkAndCancelTest extends UseCaseTest {
    private final LikeMarkingValidator likeMarkingValidator = mock(LikeMarkingValidator.class);
    private final FavoriteRepository favoriteRepository = mock(FavoriteRepository.class);
    private final MarkStudyLikeUseCase likeSut = new MarkStudyLikeUseCase(likeMarkingValidator, favoriteRepository);
    private final CancelStudyLikeUseCase cancelSut = new CancelStudyLikeUseCase(likeMarkingValidator, favoriteRepository);

    private static final Long MEMBER_ID = 1L;
    private static final Long STUDY_ID = 1L;

    @Nested
    @DisplayName("찜 등록")
    class LikeMarking {
        private final MarkStudyLikeCommand command = new MarkStudyLikeCommand(MEMBER_ID, STUDY_ID);

        @Test
        @DisplayName("이미 찜 등록된 스터디를 찜할 수 없다")
        void throwExceptionByAlreadyLikeMarked() {
            // given
            given(likeMarkingValidator.alreadyLikeMarked(MEMBER_ID, STUDY_ID)).willReturn(true);

            // when - then
            assertThatThrownBy(() -> likeSut.invoke(command))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(FavoriteErrorCode.ALREADY_LIKE_MARKED.getMessage());

            assertAll(
                    () -> verify(likeMarkingValidator, times(1)).alreadyLikeMarked(MEMBER_ID, STUDY_ID),
                    () -> verify(favoriteRepository, times(0)).save(any(Favorite.class))
            );
        }

        @Test
        @DisplayName("해당 스터디를 찜 등록한다")
        void success() {
            // given
            given(likeMarkingValidator.alreadyLikeMarked(MEMBER_ID, STUDY_ID)).willReturn(false);

            final Favorite favorite = Favorite.favoriteMarking(MEMBER_ID, STUDY_ID).apply(1L);
            given(favoriteRepository.save(any(Favorite.class))).willReturn(favorite);

            // when
            final Long savedFavoriteId = likeSut.invoke(command);

            // then
            assertAll(
                    () -> verify(likeMarkingValidator, times(1)).alreadyLikeMarked(MEMBER_ID, STUDY_ID),
                    () -> verify(favoriteRepository, times(1)).save(any(Favorite.class)),
                    () -> assertThat(savedFavoriteId).isEqualTo(favorite.getId())
            );
        }
    }

    @Nested
    @DisplayName("찜 취소")
    class LikeCancellation {
        private final CancelStudyLikeCommand command = new CancelStudyLikeCommand(MEMBER_ID, STUDY_ID);

        @Test
        @DisplayName("찜 등록이 되지 않은 스터디를 취소할 수 없다")
        void throwExceptionByNeverLikeMarked() {
            // given
            given(likeMarkingValidator.neverLikeMarked(MEMBER_ID, STUDY_ID)).willReturn(true);

            // when - then
            assertThatThrownBy(() -> cancelSut.invoke(command))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(FavoriteErrorCode.NEVER_LIKE_MARKED.getMessage());

            assertAll(
                    () -> verify(likeMarkingValidator, times(1)).neverLikeMarked(MEMBER_ID, STUDY_ID),
                    () -> verify(favoriteRepository, times(0)).cancelLikeMarking(MEMBER_ID, STUDY_ID)
            );
        }

        @Test
        @DisplayName("해당 스터디에 대해서 등록한 찜을 취소한다")
        void success() {
            // given
            given(likeMarkingValidator.neverLikeMarked(MEMBER_ID, STUDY_ID)).willReturn(false);

            // when
            cancelSut.invoke(command);

            // then
            assertAll(
                    () -> verify(likeMarkingValidator, times(1)).neverLikeMarked(MEMBER_ID, STUDY_ID),
                    () -> verify(favoriteRepository, times(1)).cancelLikeMarking(MEMBER_ID, STUDY_ID)
            );
        }
    }
}
