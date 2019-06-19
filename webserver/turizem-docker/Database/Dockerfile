FROM alpine/git as clone
WORKDIR /git_repo

ENV GIT_URL=https://{YOUR_TOKEN}:@github.com/ajdapretnar/BibMine.git
RUN git clone --branch add_webserver $GIT_URL\
	&& cd BibMine\
	&& git pull

WORKDIR /turizem-database
RUN cp -r /git_repo/BibMine/webserver/turizem-database/* /turizem-database
RUN cp /git_repo/BibMine/webserver/turizem-database/01-db.sql /turizem-database 
RUN rm -rf /git_repo 

FROM postgres as database
COPY --from=clone /turizem-database/01-db.sql /docker-entrypoint-initdb.d/

EXPOSE 5432
