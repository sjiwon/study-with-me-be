package com.kgu.studywithme.global.query;

public record SliceResponse<T>(
        T result,
        boolean hasNext
) {
}
