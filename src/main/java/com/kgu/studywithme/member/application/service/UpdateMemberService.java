package com.kgu.studywithme.member.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.adapter.MemberDuplicateCheckRepositoryAdapter;
import com.kgu.studywithme.member.application.usecase.command.UpdateMemberUseCase;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class UpdateMemberService implements UpdateMemberUseCase {
    private final MemberReader memberReader;
    private final MemberDuplicateCheckRepositoryAdapter memberDuplicateCheckRepositoryAdapter;

    @Override
    public void invoke(final Command command) {
        final Member member = memberReader.getById(command.memberId());
        validateNicknameIsUnique(command.memberId(), command.nickname());
        validatePhoneIsUnique(command.memberId(), command.phone());

        member.update(
                command.nickname(),
                command.phone(),
                command.province(),
                command.city(),
                command.emailOptIn(),
                command.interests()
        );
    }

    public void validateNicknameIsUnique(final Long memberId, final String nickname) {
        if (memberDuplicateCheckRepositoryAdapter.isNicknameUsedByOther(memberId, nickname)) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

    public void validatePhoneIsUnique(final Long memberId, final String phone) {
        if (memberDuplicateCheckRepositoryAdapter.isPhoneUsedByOther(memberId, phone)) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_PHONE);
        }
    }
}
