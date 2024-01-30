package com.kgu.studywithme.global.query;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface PageCreator {
    int SLICE_PER_PAGE = 8;

    static Pageable query(final int page) {
        return PageRequest.of(page, SLICE_PER_PAGE);
    }
}
