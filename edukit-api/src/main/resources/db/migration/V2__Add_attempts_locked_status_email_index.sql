-- Add attempts column to verification_code table and create member email index
ALTER TABLE verification_code 
ADD COLUMN attempts INT NULL DEFAULT 0;

-- Add LOCKED status to verification_code status enum
ALTER TABLE verification_code 
MODIFY COLUMN status ENUM('VERIFIED', 'EXPIRED', 'PENDING', 'LOCKED') NOT NULL;

-- Add index on member email column for better login performance
CREATE INDEX idx_member_email ON member (email);