package com.kgu.studywithme.member.application;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Email;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberValidator {
    private final MemberRepository memberRepository;

    public void validateEmail(final Email email) {
        if (memberRepository.existsByEmail(email)) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_EMAIL);
        }
    }

    public void validateNickname(final Nickname nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

    public void validateNicknameForModify(
            final Long memberId,
            final Nickname nickname
    ) {
        if (memberRepository.existsByIdNotAndNickname(memberId, nickname)) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

    public void validatePhone(final String phone) {
        if (memberRepository.existsByPhone(phone)) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_PHONE);
        }
    }

    public void validatePhoneForModify(
            final Long memberId,
            final String phone
    ) {
        if (memberRepository.existsByIdNotAndPhone(memberId, phone)) {
            throw StudyWithMeException.type(MemberErrorCode.DUPLICATE_PHONE);
        }
    }
}
