-- Drop student_record_ai_result table and add member relation to student_record_ai_task

-- Drop the student_record_ai_result table
DROP TABLE IF EXISTS student_record_ai_result;

-- Add member_id column to student_record_ai_task table with foreign key constraint
ALTER TABLE student_record_ai_task 
ADD COLUMN member_id bigint NOT NULL AFTER student_record_ai_task_id,
ADD CONSTRAINT FK_student_record_ai_task_member 
    FOREIGN KEY (member_id) REFERENCES member (member_id);