package com.kgu.studywithme.global.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleResponseWrapper<T> {
    private final T result;

    public static <T> SimpleResponseWrapper<T> of(T result) {
        return new SimpleResponseWrapper<>(result);
    }
}
