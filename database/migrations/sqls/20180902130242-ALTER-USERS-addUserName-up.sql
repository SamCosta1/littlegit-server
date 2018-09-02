ALTER TABLE Users
ADD COLUMN username VARCHAR(20);

UPDATE Users
SET username=Users.id;