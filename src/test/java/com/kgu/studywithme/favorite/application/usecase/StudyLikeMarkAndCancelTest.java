package com.kgu.studywithme.favorite.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.favorite.application.usecase.command.CancelStudyLikeCommand;
import com.kgu.studywithme.favorite.application.usecase.command.MarkStudyLikeCommand;
import com.kgu.studywithme.favorite.domain.model.Favorite;
import com.kgu.studywithme.favorite.domain.repository.FavoriteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Favorite -> MarkStudyLikeUseCase, CancelStudyLikeUseCase 테스트")
class StudyLikeMarkAndCancelTest extends UseCaseTest {
    private final FavoriteRepository favoriteRepository = mock(FavoriteRepository.class);
    private final MarkStudyLikeUseCase likeSut = new MarkStudyLikeUseCase(favoriteRepository);
    private final CancelStudyLikeUseCase cancelSut = new CancelStudyLikeUseCase(favoriteRepository);

    private final MarkStudyLikeCommand likeCommand = new MarkStudyLikeCommand(1L, 1L);
    private final CancelStudyLikeCommand cancelCommand = new CancelStudyLikeCommand(1L, 1L);

    @Test
    @DisplayName("해당 스터디를 찜 등록한다")
    void likeMarking() {
        // given
        final Favorite favorite = likeCommand.toDomain().apply(1L);
        given(favoriteRepository.save(any(Favorite.class))).willReturn(favorite);

        // when
        final Long savedFavoriteId = likeSut.invoke(likeCommand);

        // then
        assertAll(
                () -> verify(favoriteRepository, times(1)).save(any(Favorite.class)),
                () -> assertThat(savedFavoriteId).isEqualTo(favorite.getId())
        );
    }

    @Test
    @DisplayName("해당 스터디에 대해서 등록한 찜을 취소한다")
    void likeCancel() {
        // when
        cancelSut.invoke(cancelCommand);

        // then
        verify(favoriteRepository, times(1)).cancelLikeMarking(cancelCommand.memberId(), cancelCommand.studyId());
    }
}
