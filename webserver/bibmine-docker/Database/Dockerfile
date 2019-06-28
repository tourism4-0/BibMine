FROM alpine/git as clone
WORKDIR /git_repo

ENV GIT_URL=https://github.com/ajdapretnar/BibMine.git
RUN git clone --branch add_webserver $GIT_URL\
	&& cd BibMine\
	&& git pull

WORKDIR /bibmine-database
RUN cp -r /git_repo/BibMine/webserver/bibmine-database/* /bibmine-database
RUN cp /git_repo/BibMine/webserver/bibmine-database/01-db.sql /bibmine-database 
RUN rm -rf /git_repo 

FROM postgres as database
COPY --from=clone /bibmine-database/01-db.sql /docker-entrypoint-initdb.d/

EXPOSE 5432
