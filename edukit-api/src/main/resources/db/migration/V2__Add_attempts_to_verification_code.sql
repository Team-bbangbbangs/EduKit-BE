-- Add attempts column to verification_code table
ALTER TABLE verification_code 
ADD COLUMN attempts INT NULL DEFAULT 0;