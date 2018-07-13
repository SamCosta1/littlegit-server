CREATE TABLE Repos (
  id  					INT NOT NULL AUTO_INCREMENT,
  repoName			VARCHAR(20),
  createdDate		DATETIME,
  creatorId			INT,
  description   VARCHAR(200),
  serverId			INT,
  cloneUrlPath	VARCHAR(50),
  PRIMARY KEY(id)
);