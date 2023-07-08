package com.kgu.studywithme.global.dto;

public record SimpleResponseWrapper<T>(
        T result
) {
}
