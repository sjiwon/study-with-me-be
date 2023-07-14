package com.kgu.studywithme.member.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.usecase.command.MemberRegistrationUseCase;
import com.kgu.studywithme.member.domain.Email;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class MemberRegistrationService implements MemberRegistrationUseCase {
    private final MemberRepository memberRepository;

    @Override
    public Long registration(final Command command) {
        validateEmailIsUnique(command.email());
        validateNicknameIsUnique(command.nickname());
        validatePhoneIsUnique(command.phone());

        return memberRepository.save(command.toEntity()).getId();
    }

    private void validateEmailIsUnique(final Email email) {
        if (memberRepository.existsByEmail(email)) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_EMAIL);
        }
    }

    private void validateNicknameIsUnique(final Nickname nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

    public void validatePhoneIsUnique(final String phone) {
        if (memberRepository.existsByPhone(phone)) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_PHONE);
        }
    }
}
