ALTER TABLE RepoAccess DROP COLUMN id;
ALTER TABLE RepoAccess ADD PRIMARY KEY (repoId, userId);