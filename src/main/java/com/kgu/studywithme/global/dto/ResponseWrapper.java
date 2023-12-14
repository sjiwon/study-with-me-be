package com.kgu.studywithme.global.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class ResponseWrapper<T> {
    private T result;

    public static <T> ResponseWrapper<T> from(final T result) {
        return new ResponseWrapper<>(result);
    }
}
