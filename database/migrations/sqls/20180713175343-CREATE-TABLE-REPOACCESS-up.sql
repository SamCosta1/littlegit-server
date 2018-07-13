CREATE TABLE RepoAccess (
	  id  				INT NOT NULL AUTO_INCREMENT,
    repoId			INT,
    userId	 		INT,
    active			BOOL,
    level				TINYINT,    
    PRIMARY KEY(id)
);
