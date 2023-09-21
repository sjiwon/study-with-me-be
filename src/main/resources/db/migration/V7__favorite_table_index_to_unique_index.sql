-- 외래키 임시 삭제 및 기존 Index 제거
ALTER TABLE favorite
    DROP FOREIGN KEY fk_favorite_member_id_from_member;

ALTER TABLE favorite
    DROP FOREIGN KEY fk_favorite_study_id_from_study;

DROP INDEX idx_favorite_member_id_study_id ON favorite;

-- 외래키 추가 및 Unique Index 적용
ALTER TABLE favorite
    ADD CONSTRAINT fk_favorite_member_id_from_member
        FOREIGN KEY (member_id)
            REFERENCES member (id);

ALTER TABLE favorite
    ADD CONSTRAINT fk_favorite_study_id_from_study
        FOREIGN KEY (study_id)
            REFERENCES study (id);

ALTER TABLE favorite
    ADD UNIQUE INDEX idx_favorite_member_id_study_id (member_id, study_id);
