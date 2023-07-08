package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class StudyLocation {
    @Column(name = "province")
    private String province;

    @Column(name = "city")
    private String city;

    private StudyLocation(
            final String province,
            final String city
    ) {
        this.province = province;
        this.city = city;
    }

    public static StudyLocation of(
            final String province,
            final String city
    ) {
        validateProvinceAndCityIsNotEmpty(province, city);
        return new StudyLocation(province, city);
    }

    private static void validateProvinceAndCityIsNotEmpty(
            final String province,
            final String city
    ) {
        if (isEmptyText(province) || isEmptyText(city)) {
            throw StudyWithMeException.type(StudyErrorCode.STUDY_LOCATION_IS_BLANK);
        }
    }

    private static boolean isEmptyText(final String str) {
        return !StringUtils.hasText(str);
    }
}
