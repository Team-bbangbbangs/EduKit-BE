-- Remove foreign key constraint and drop student_record_id column
-- This completely decouples the relationship between StudentRecordAITask and StudentRecord entities

ALTER TABLE student_record_ai_task 
DROP FOREIGN KEY FK_ai_task_to_record;

ALTER TABLE student_record_ai_task 
DROP COLUMN student_record_id;