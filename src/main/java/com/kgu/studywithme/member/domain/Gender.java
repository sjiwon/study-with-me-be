package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE("M", "남성"),
    FEMALE("F", "여성"),
    ;

    private final String simpleValue;
    private final String value;

    public static Gender from(final String simpleValue) {
        return Arrays.stream(values())
                .filter(gender -> gender.simpleValue.equals(simpleValue))
                .findFirst()
                .orElseThrow(() -> StudyWithMeException.type(MemberErrorCode.INVALID_GENDER));
    }
}
