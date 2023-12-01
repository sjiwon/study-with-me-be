# 기존 조회 인덱스 제거
ALTER TABLE study
    DROP INDEX idx_study_category_is_terminated;
ALTER TABLE study
    DROP INDEX idx_study_study_type_category_is_terminated;
ALTER TABLE study
    DROP INDEX idx_study_province_city_category_is_terminated;
ALTER TABLE study
    DROP INDEX idx_study_province_city_study_type_category_is_terminated;

# 반정규화 필드 포함 인덱스 추가
ALTER TABLE study
    ADD INDEX idx_study_category_is_terminated (category, is_terminated);
ALTER TABLE study
    ADD INDEX idx_study_category_is_terminated_favorite_count (category, is_terminated, favorite_count);
ALTER TABLE study
    ADD INDEX idx_study_category_is_terminated_review_count (category, is_terminated, review_count);
ALTER TABLE study
    ADD INDEX idx_study_study_type_category_is_terminated (study_type, category, is_terminated);
ALTER TABLE study
    ADD INDEX idx_study_study_type_category_is_terminated_favorite_count (study_type, category, is_terminated, favorite_count);
ALTER TABLE study
    ADD INDEX idx_study_study_type_category_is_terminated_review_count (study_type, category, is_terminated, review_count);
