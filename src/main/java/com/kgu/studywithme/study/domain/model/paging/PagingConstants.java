package com.kgu.studywithme.study.domain.model.paging;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface PagingConstants {
    int SLICE_PER_PAGE = 8;

    static Pageable getDefaultPageRequest(final int page) {
        return PageRequest.of(page, SLICE_PER_PAGE);
    }
}
