CREATE TABLE list_properties (
 _id INTEGER PRIMARY KEY,
 name TEXT,
 user_created INT,
 icon INT,
 color INT,
 download_sheet INT,
 download_track INT
);

INSERT INTO list_properties (name, user_created, icon, color, download_sheet, download_track)
VALUES
  ("Favorites", 0, 1, -1801946, 1, 0),
  ("Teachable", 0, 8, -7617718, 1, 0);

PRAGMA foreign_keys=off;

BEGIN TRANSACTION;

CREATE TABLE list_entries (
  tag_db_id TEXT,
  list_id TEXT,
  FOREIGN KEY(tag_db_id) REFERENCES tag(_id),
  FOREIGN KEY(list_id) REFERENCES list_properties(_id),
  PRIMARY KEY (tag_db_id, list_id)
);

INSERT INTO list_entries (tag_db_id, list_id)
  SELECT favorites.favorite_tag_id, list_properties._id
  FROM favorites, list_properties
  WHERE list_properties.name = "Favorites";

COMMIT;

PRAGMA foreign_keys=on;

DROP TABLE favorites;