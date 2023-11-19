package com.kgu.studywithme.favorite.application.usecase;

import com.kgu.studywithme.favorite.application.usecase.command.CancelStudyLikeCommand;
import com.kgu.studywithme.favorite.application.usecase.command.MarkStudyLikeCommand;
import com.kgu.studywithme.favorite.domain.model.Favorite;
import com.kgu.studywithme.favorite.domain.repository.FavoriteRepository;
import com.kgu.studywithme.favorite.exception.FavoriteErrorCode;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManageFavoriteUseCase {
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;
    private final FavoriteRepository favoriteRepository;

    @StudyWithMeWritableTransactional
    public Long markLike(final MarkStudyLikeCommand command) {
        final Study study = studyRepository.getById(command.studyId());
        final Member member = memberRepository.getById(command.memberId());

        try {
            return favoriteRepository.save(Favorite.favoriteMarking(member, study)).getId();
        } catch (final DataIntegrityViolationException e) {
            throw StudyWithMeException.type(FavoriteErrorCode.ALREADY_LIKE_MARKED);
        }
    }

    @StudyWithMeWritableTransactional
    public void cancelLike(final CancelStudyLikeCommand command) {
        final Favorite favorite = favoriteRepository.findByStudyIdAndMemberId(command.studyId(), command.memberId())
                .orElseThrow(() -> StudyWithMeException.type(FavoriteErrorCode.FAVORITE_MARKING_NOT_FOUND));
        favoriteRepository.delete(favorite);
    }
}
