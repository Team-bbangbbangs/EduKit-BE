-- Add failure tracking columns to student_record_ai_task table

ALTER TABLE student_record_ai_task
    ADD COLUMN failed_at DATETIME NULL AFTER completed_at,
    ADD COLUMN error_type VARCHAR(50) NULL AFTER failed_at;
