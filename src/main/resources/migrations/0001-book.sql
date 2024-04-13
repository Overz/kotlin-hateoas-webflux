CREATE SEQUENCE book_id_seq START 1 INCREMENT BY 1;

CREATE TABLE book
(
	cdBook        INTEGER     NOT NULL DEFAULT nextval('book_id_seq'),
	nmBook        TEXT        NOT NULL,
	nmAuthor      TEXT        NOT NULL,
	deDescription TEXT        NOT NULL,
	dtCreatedAt   TIMESTAMPTZ NOT NULL DEFAULT now(),
	dtupdatedAt   TIMESTAMPTZ,

	CONSTRAINT pk_book PRIMARY KEY (cdBook)
);

CREATE UNIQUE INDEX book_un_idx_nmbook ON book (nmBook);

INSERT INTO book (nmBook, nmAuthor, deDescription)
VALUES ('O Senhor dos Anéis', 'J.R.R. Tolkien', 'Um épico de fantasia que se passa na Terra Média.'),
			 ('Harry Potter e a Pedra Filosofal', 'J.K. Rowling', 'O primeiro livro da série Harry Potter.'),
			 ('Cem Anos de Solidão', 'Gabriel García Márquez', 'Um romance que conta a história da família Buendía.'),
			 ('Dom Quixote', 'Miguel de Cervantes', 'Um clássico da literatura espanhola.'),
			 ('1984', 'George Orwell', 'Uma distopia sobre um governo totalitário.'),
			 ('Harry Potter e a Câmara Secreta', 'J.K. Rowling', 'O segundo livro da série Harry Potter.'),
			 ('Crime e Castigo', 'Fiódor Dostoiévski', 'Um romance que explora a psicologia humana.'),
			 ('O Hobbit', 'J.R.R. Tolkien', 'Uma história que precede O Senhor dos Anéis.'),
			 ('O Guia do Mochileiro das Galáxias', 'Douglas Adams', 'Uma comédia de ficção científica.'),
			 ('O Pequeno Príncipe', 'Antoine de Saint-Exupéry', 'Um conto filosófico sobre amizade e amor.');
