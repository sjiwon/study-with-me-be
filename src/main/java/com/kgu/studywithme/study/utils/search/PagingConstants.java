package com.kgu.studywithme.study.utils.search;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface PagingConstants {
    int SLICE_PER_PAGE = 8;

    static Pageable createPageRequest(final int page) {
        return PageRequest.of(page, SLICE_PER_PAGE);
    }
}
