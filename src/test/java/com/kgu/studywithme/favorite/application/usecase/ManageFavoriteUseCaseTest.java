package com.kgu.studywithme.favorite.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.favorite.application.usecase.command.CancelStudyLikeCommand;
import com.kgu.studywithme.favorite.application.usecase.command.MarkStudyLikeCommand;
import com.kgu.studywithme.favorite.domain.model.Favorite;
import com.kgu.studywithme.favorite.domain.repository.FavoriteRepository;
import com.kgu.studywithme.favorite.exception.FavoriteErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Favorite -> ManageFavoriteUseCase 테스트")
class ManageFavoriteUseCaseTest extends UseCaseTest {
    private final StudyRepository studyRepository = mock(StudyRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final FavoriteRepository favoriteRepository = mock(FavoriteRepository.class);
    private final ManageFavoriteUseCase sut = new ManageFavoriteUseCase(
            studyRepository,
            memberRepository,
            favoriteRepository
    );

    private final Member host = JIWON.toMember().apply(1L);

    @Nested
    @DisplayName("스터디 찜 등록")
    class MarkLike {
        private final Study study = SPRING.toStudy(host).apply(1L);
        private final MarkStudyLikeCommand command = new MarkStudyLikeCommand(host.getId(), study.getId());

        @Test
        @DisplayName("해당 스터디를 찜 등록한다")
        void success() {
            // given
            given(studyRepository.getById(command.studyId())).willReturn(study);
            given(memberRepository.getById(command.memberId())).willReturn(host);

            final Favorite favorite = Favorite.favoriteMarking(host, study);
            given(favoriteRepository.save(any(Favorite.class))).willReturn(favorite);

            // when
            final Long savedFavoriteId = sut.markLike(command);

            // then
            assertAll(
                    () -> verify(studyRepository, times(1)).getById(command.studyId()),
                    () -> verify(memberRepository, times(1)).getById(command.memberId()),
                    () -> verify(studyRepository, times(1)).increaseFavoriteCount(study.getId()),
                    () -> verify(favoriteRepository, times(1)).save(any(Favorite.class)),
                    () -> assertThat(savedFavoriteId).isEqualTo(favorite.getId())
            );
        }
    }

    @Nested
    @DisplayName("스터디 찜 등록 취소")
    class CancelLike {
        private Study study;
        private CancelStudyLikeCommand command;

        @BeforeEach
        void setUp() {
            study = SPRING.toStudy(host).apply(1L);
            command = new CancelStudyLikeCommand(host.getId(), study.getId());
        }

        @Test
        @DisplayName("찜 등록한 기록이 없다면 취소할 수 없다")
        void throwExceptionByFavoriteRecordNotFound() {
            // given
            given(studyRepository.getById(command.studyId())).willReturn(study);
            doThrow(StudyWithMeException.type(FavoriteErrorCode.FAVORITE_MARKING_NOT_FOUND))
                    .when(favoriteRepository)
                    .getFavoriteRecord(study.getId(), command.memberId());

            // when - then
            assertThatThrownBy(() -> sut.cancelLike(command))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(FavoriteErrorCode.FAVORITE_MARKING_NOT_FOUND.getMessage());

            assertAll(
                    () -> verify(studyRepository, times(1)).getById(command.studyId()),
                    () -> verify(favoriteRepository, times(1)).getFavoriteRecord(study.getId(), command.memberId()),
                    () -> verify(studyRepository, times(0)).decreaseFavoriteCount(study.getId()),
                    () -> verify(favoriteRepository, times(0)).delete(any(Favorite.class))
            );
        }

        @Test
        @DisplayName("찜 등록을 취소한다")
        void success() {
            // given
            given(studyRepository.getById(command.studyId())).willReturn(study);

            final Favorite favorite = Favorite.favoriteMarking(host, study);
            given(favoriteRepository.getFavoriteRecord(study.getId(), command.memberId())).willReturn(favorite);

            // when
            sut.cancelLike(command);

            // then
            assertAll(
                    () -> verify(studyRepository, times(1)).getById(command.studyId()),
                    () -> verify(favoriteRepository, times(1)).getFavoriteRecord(study.getId(), command.memberId()),
                    () -> verify(studyRepository, times(1)).decreaseFavoriteCount(study.getId()),
                    () -> verify(favoriteRepository, times(1)).delete(favorite)
            );
        }
    }
}
