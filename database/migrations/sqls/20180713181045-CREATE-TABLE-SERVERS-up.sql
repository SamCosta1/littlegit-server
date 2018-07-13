CREATE TABLE GitServers (
	id  				INT NOT NULL AUTO_INCREMENT,
  ip					VARCHAR(20),
  region			VARCHAR(10),
  capacity		INT,
  PRIMARY KEY(id)
);