-- Drop existing foreign key constraint
ALTER TABLE student_record 
DROP FOREIGN KEY FK_student_record_student;

-- Add foreign key constraint with ON DELETE CASCADE
ALTER TABLE student_record 
ADD CONSTRAINT FK_student_record_student 
FOREIGN KEY (student_id) REFERENCES student (student_id) ON DELETE CASCADE;
