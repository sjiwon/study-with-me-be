package com.kgu.studywithme.global.cache;

import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.study.utils.search.SearchSortType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CacheKeyGenerator {
    private static final String DELIMITER = ":";

    public static String createStudyKey(
            final Category category,
            final SearchSortType sort,
            final String type,
            final String province,
            final String city,
            final int page
    ) {
        final StringBuilder result = new StringBuilder()
                .append(category)
                .append(DELIMITER)
                .append(sort);

        if (exists(type)) {
            result.append(DELIMITER).append(type);
        }

        if (exists(province)) {
            result.append(DELIMITER).append(province);
        }

        if (exists(city)) {
            result.append(DELIMITER).append(city);
        }

        result.append(DELIMITER).append(page);
        return result.toString();
    }

    public static String createStudyKey(
            final Long memberId,
            final SearchSortType sort,
            final String type,
            final String province,
            final String city,
            final int page
    ) {
        final StringBuilder result = new StringBuilder()
                .append(memberId)
                .append(DELIMITER)
                .append(sort);

        if (exists(type)) {
            result.append(DELIMITER).append(type);
        }

        if (exists(province)) {
            result.append(DELIMITER).append(province);
        }

        if (exists(city)) {
            result.append(DELIMITER).append(city);
        }

        result.append(DELIMITER).append(page);
        return result.toString();
    }

    private static boolean exists(final String value) {
        return StringUtils.hasText(value);
    }
}
