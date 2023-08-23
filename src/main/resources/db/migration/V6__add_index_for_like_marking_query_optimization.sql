-- 스터디 찜 관련 쿼리 최적화
ALTER TABLE favorite
    ADD INDEX idx_favorite_member_id_study_id (member_id, study_id);
