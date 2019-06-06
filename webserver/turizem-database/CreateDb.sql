CREATE SCHEMA "public" IF NOT EXISTS "public";

CREATE SEQUENCE "public".articles_id_seq START WITH 1;

CREATE SEQUENCE "public".queries_id_seq START WITH 1;

CREATE TABLE "public".article ( 
	id                   serial  NOT NULL ,
	aid                  char(255)   ,
	json                 bytea   ,
	xml                  bytea   ,
	CONSTRAINT pk_articles_id PRIMARY KEY ( id )
 );

CREATE TABLE "public".query ( 
	id                   serial  NOT NULL ,
	query                char(255)   ,
	CONSTRAINT pk_queries_id PRIMARY KEY ( id )
 );

CREATE TABLE "public".article_query ( 
	article_id           integer  NOT NULL ,
	query_id             integer  NOT NULL ,
	CONSTRAINT primary_keys PRIMARY KEY ( article_id, query_id )
 );

CREATE INDEX "idx_article_query_article_id" ON "public".article_query ( article_id );

CREATE INDEX "idx_article_query_query_id" ON "public".article_query ( query_id );

ALTER TABLE "public".article_query ADD CONSTRAINT fk_article_query_queries FOREIGN KEY ( query_id ) REFERENCES "public".query( id ) ON DELETE CASCADE;

ALTER TABLE "public".article_query ADD CONSTRAINT fk_article_query_articles FOREIGN KEY ( article_id ) REFERENCES "public".article( id ) ON DELETE CASCADE;

