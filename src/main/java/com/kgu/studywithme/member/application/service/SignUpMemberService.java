package com.kgu.studywithme.member.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.adapter.MemberDuplicateCheckRepository;
import com.kgu.studywithme.member.application.usecase.command.SignUpMemberUseCase;
import com.kgu.studywithme.member.domain.Email;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class SignUpMemberService implements SignUpMemberUseCase {
    private final MemberDuplicateCheckRepository memberDuplicateCheckRepository;
    private final MemberRepository memberRepository;

    @Override
    public Long invoke(final Command command) {
        validateEmailIsUnique(command.email());
        validateNicknameIsUnique(command.nickname());
        validatePhoneIsUnique(command.phone());

        return memberRepository.save(command.toDomain()).getId();
    }

    private void validateEmailIsUnique(final Email email) {
        if (memberDuplicateCheckRepository.isEmailExists(email.getValue())) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_EMAIL);
        }
    }

    private void validateNicknameIsUnique(final Nickname nickname) {
        if (memberDuplicateCheckRepository.isNicknameExists(nickname.getValue())) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

    public void validatePhoneIsUnique(final String phone) {
        if (memberDuplicateCheckRepository.isPhoneExists(phone)) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_PHONE);
        }
    }
}
