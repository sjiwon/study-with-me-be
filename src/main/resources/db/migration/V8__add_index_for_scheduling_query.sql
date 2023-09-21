ALTER TABLE study_attendance
    ADD INDEX idx_study_attendance_status_study_id_week_participant_id (status, study_id, week, participant_id);

ALTER TABLE study_weekly
    ADD INDEX idx_study_weekly_end_date_is_auto_attendance_study_id_week (end_date, is_auto_attendance, study_id, week);
