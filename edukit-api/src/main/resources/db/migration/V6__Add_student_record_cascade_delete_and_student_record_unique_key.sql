-- Drop existing foreign key constraint
ALTER TABLE student_record 
DROP FOREIGN KEY FK_student_record_student;

-- Add foreign key constraint with ON DELETE CASCADE
ALTER TABLE student_record 
ADD CONSTRAINT FK_student_record_student 
FOREIGN KEY (student_id) REFERENCES student (student_id) ON DELETE CASCADE;

-- Add unique constraint for student_record table on (student_id, student_record_type) combination
ALTER TABLE student_record
ADD CONSTRAINT UQ_student_record_student_type UNIQUE (student_id, student_record_type);
