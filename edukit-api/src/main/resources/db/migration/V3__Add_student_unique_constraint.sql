-- Add unique constraint for student table on (member_id, grade, class_number, student_number) combination
ALTER TABLE student 
ADD CONSTRAINT UQ_student_member_grade_class_number UNIQUE (member_id, grade, class_number, student_number);