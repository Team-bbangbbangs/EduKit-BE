-- Remove foreign key constraint between student_record_ai_task and student_record
-- This allows decoupling the relationship between StudentRecordAITask and StudentRecord entities

ALTER TABLE student_record_ai_task 
DROP FOREIGN KEY FK_ai_task_to_record;