package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Region {
    @Column(name = "province", nullable = false)
    private String province;

    @Column(name = "city", nullable = false)
    private String city;

    private Region(
            final String province,
            final String city
    ) {
        this.province = province;
        this.city = city;
    }

    public static Region of(
            final String province,
            final String city
    ) {
        validateProvinceAndCityIsNotEmpty(province, city);
        return new Region(province, city);
    }

    public Region update(
            final String province,
            final String city
    ) {
        validateProvinceAndCityIsNotEmpty(province, city);
        return new Region(province, city);
    }

    private static void validateProvinceAndCityIsNotEmpty(
            final String province,
            final String city
    ) {
        if (isEmptyText(province) || isEmptyText(city)) {
            throw StudyWithMeException.type(MemberErrorCode.REGION_IS_BLANK);
        }
    }

    private static boolean isEmptyText(final String str) {
        return !StringUtils.hasText(str);
    }
}
