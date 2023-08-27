package com.kgu.studywithme.member.application.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.adapter.MemberDuplicateCheckRepositoryAdapter;
import com.kgu.studywithme.member.application.usecase.command.SignUpMemberUseCase;
import com.kgu.studywithme.member.domain.Email;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.member.domain.Phone;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpMemberService implements SignUpMemberUseCase {
    private final MemberDuplicateCheckRepositoryAdapter memberDuplicateCheckRepositoryAdapter;
    private final MemberRepository memberRepository;

    @Override
    public Long invoke(final Command command) {
        validateEmailIsUnique(command.email());
        validateNicknameIsUnique(command.nickname());
        validatePhoneIsUnique(command.phone());

        return memberRepository.save(command.toDomain()).getId();
    }

    private void validateEmailIsUnique(final Email email) {
        if (memberDuplicateCheckRepositoryAdapter.isEmailExists(email.getValue())) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_EMAIL);
        }
    }

    private void validateNicknameIsUnique(final Nickname nickname) {
        if (memberDuplicateCheckRepositoryAdapter.isNicknameExists(nickname.getValue())) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

    public void validatePhoneIsUnique(final Phone phone) {
        if (memberDuplicateCheckRepositoryAdapter.isPhoneExists(phone.getValue())) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_PHONE);
        }
    }
}
