-- 신청자 Validation 관련 쿼리 최적화
ALTER TABLE study_participant
    ADD INDEX idx_participant_study_id_member_id_status (study_id, member_id, status);

-- 출석 횟수 조회 쿼리 최적화
ALTER TABLE study_attendance
    ADD INDEX idx_attendance_study_id_participant_id_status (study_id, participant_id, status);
