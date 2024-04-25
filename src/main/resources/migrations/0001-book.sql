-- QUERY PARA EXIBIR TRIGRAM ACEITOS
-- SELECT am.amname AS index_method,
-- 			 opc.opcname AS opclass_name,
-- 			 opc.opcintype::regtype AS indexed_type,
-- 			 opc.opcdefault AS is_default
-- FROM pg_am am, pg_opclass opc
-- WHERE opc.opcmethod = am.oid
-- ORDER BY index_method, opclass_name;

CREATE SEQUENCE book_id_seq START 1 INCREMENT BY 1;

CREATE TABLE book
(
	cdBook        INTEGER NOT NULL DEFAULT nextval('book_id_seq'),
	nmBook        TEXT    NOT NULL,
	nmAuthor      TEXT    NOT NULL,
	deDescription TEXT    NOT NULL,
	dtCreatedAt   DATE    NOT NULL DEFAULT now()::DATE,
	dtupdatedAt   DATE,

	CONSTRAINT pk_book PRIMARY KEY (cdBook)
);

CREATE UNIQUE INDEX book_un_idx_nmbook ON book (nmBook);
CREATE INDEX book_idx_trgm_dedescricao ON book USING btree (deDescription text_pattern_ops);

CREATE OR REPLACE FUNCTION trgr_tbl_book_update_dtupdatedat_to_now()
	RETURNS TRIGGER AS
$$
BEGIN
	NEW.dtUpdatedAt := NOW()::DATE;
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trgr_tbl_book_update_dtupdatedat_to_now
	BEFORE UPDATE
	ON book
	FOR EACH ROW
EXECUTE FUNCTION trgr_tbl_book_update_dtupdatedat_to_now();

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
