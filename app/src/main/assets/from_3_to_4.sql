CREATE TABLE video (
 _id INTEGER PRIMARY KEY,
 video_id INT,
 title TEXT,
 description TEXT,
 key TEXT,
 video_code TEXT,
 sung_by TEXT,
 sung_website TEXT,
 posted_date TEXT,
 video_tag_id TEXT,
 view_count TEXT,
 like_count TEXT,
 dislike_count TEXT,
 favorite_count TEXT,
 comment_count TEXT,
 multitrack INT,
 FOREIGN KEY(video_tag_id) REFERENCES tag(_id)
);
ALTER TABLE tag ADD COLUMN last_modified_date TEXT;