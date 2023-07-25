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

    public StudyLocation(
            final String province,
            final String city
    ) {
        validateProvinceAndCityIsNotEmpty(province, city);
        this.province = province;
        this.city = city;
    }

    private void validateProvinceAndCityIsNotEmpty(
            final String province,
            final String city
    ) {
        if (isEmptyText(province) || isEmptyText(city)) {
            throw StudyWithMeException.type(StudyErrorCode.STUDY_LOCATION_IS_BLANK);
        }
    }

    private boolean isEmptyText(final String str) {
        return !StringUtils.hasText(str);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final StudyLocation other = (StudyLocation) o;

        if (!province.equals(other.province)) return false;
        return city.equals(other.city);
    }

    @Override
    public int hashCode() {
        int result = province.hashCode();
        result = 31 * result + city.hashCode();
        return result;
    }
}
