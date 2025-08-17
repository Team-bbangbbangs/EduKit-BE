-- Add unique constraint for student_record table on (student_id, student_record_type) combination
ALTER TABLE student_record 
ADD CONSTRAINT UQ_student_record_student_type UNIQUE (student_id, student_record_type);
