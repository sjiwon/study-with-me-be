-- 스터디 해시태그 조회 쿼리 최적화
ALTER TABLE study_hashtag
    ADD INDEX idx_hashtag_study_id_name (study_id, name);

-- 스터디 리뷰 조회 쿼리 최적화
ALTER TABLE study_review
    ADD INDEX idx_study_review_study_id (study_id);
