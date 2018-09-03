CREATE TABLE SshKeys (
	id						INT NOT NULL AUTO_INCREMENT,
    publicKey 		LONGTEXT,
    userId				INT,
    active				BOOL,
    PRIMARY KEY (id)
);