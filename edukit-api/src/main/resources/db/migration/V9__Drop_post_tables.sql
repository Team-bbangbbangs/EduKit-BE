-- Drop post tables

-- Drop post_like first (because it depends on post, member)
DROP TABLE IF EXISTS post_like;

-- Drop post_file (depends on post)
DROP TABLE IF EXISTS post_file;

-- Drop post_comment (depends on post, member)
DROP TABLE IF EXISTS post_comment;

-- Finally drop post
DROP TABLE IF EXISTS post;
