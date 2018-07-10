CREATE TABLE UserTokens (
	id  						INT NOT NULL AUTO_INCREMENT,
  userId          INT,
  token           VARCHAR(100),
  tokenType       TINYINT,
  expiry          DATETIME,
  PRIMARY KEY (id)
);
