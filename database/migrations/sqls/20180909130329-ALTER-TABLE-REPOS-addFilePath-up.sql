ALTER TABLE Repos
ADD COLUMN filePath VARCHAR(50);
UPDATE Repos SET filePath= SUBSTRING_INDEX(cloneUrlPath, ':',  -1)