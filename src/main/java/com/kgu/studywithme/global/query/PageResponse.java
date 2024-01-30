package com.kgu.studywithme.global.query;

public record PageResponse<T>(
        T result,
        long totalCount,
        boolean hasNext
) {
}
