CREATE TABLE book
(
	cdBook        INT      NOT NULL AUTO_INCREMENT,
	nmBook        VARCHAR  NOT NULL,
	nmAuthor      VARCHAR  NOT NULL,
	deDescription VARCHAR  NOT NULL,
	dtCreatedAt   DATETIME NOT NULL DEFAULT NOW(),
	dtUpdatedAt   DATETIME,

	CONSTRAINT pk_book PRIMARY KEY (cdBook)
);
