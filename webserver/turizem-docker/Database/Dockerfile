FROM alpine/git as clone
WORKDIR /turizem-database
ENV BACKEND_GIT_URL=https://XXXXXXXXXXXXXXXXXXXXX@github.com/ajdapretnar/BibMine/webserver/turizem-database.git
RUN git clone --branch add-webserver $BACKEND_GIT_URL

FROM postgres as database

COPY --from=clone /turizem-database/turizem-database/CreateDb.sql /docker-entrypoint-initdb.d/

EXPOSE 5432
