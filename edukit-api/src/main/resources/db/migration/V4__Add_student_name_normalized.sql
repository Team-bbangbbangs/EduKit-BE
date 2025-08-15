-- Add studentNameNormalized column to student table
ALTER TABLE student
ADD COLUMN student_name_normalized VARCHAR(255) NOT NULL DEFAULT '' AFTER student_name;
