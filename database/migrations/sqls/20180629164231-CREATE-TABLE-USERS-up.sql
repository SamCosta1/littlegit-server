CREATE TABLE Users (
	id  						  INT NOT NULL AUTO_INCREMENT,
    firstName 			VARCHAR(50),
    surname 				VARCHAR(50),
    email 					VARCHAR(50),
    passwordHash    VARCHAR(100),
    passwordSalt		VARCHAR(100),
    role						TINYINT,
    languageCode	  VARCHAR(20),
    PRIMARY KEY (id)
);
