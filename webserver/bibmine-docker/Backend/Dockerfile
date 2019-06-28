FROM alpine/git as clone
WORKDIR /git_repo

ENV GIT_URL=https://github.com/ajdapretnar/BibMine.git
RUN git clone --branch add_webserver $GIT_URL 

WORKDIR /bibmine-backend
RUN cp -r /git_repo/BibMine/webserver/bibmine-backend/* /bibmine-backend 
RUN rm -rf /git_repo 

FROM maven:3.5-jdk-8-alpine as build
WORKDIR /bibmine-backend
COPY --from=clone /bibmine-backend /bibmine-backend

ENV HOST_URL=localhost:3000 \
    PORT=3000 \
	KEYCLOAK_REALM=bibmine \
    KEYCLOAK_SERVER=http://localhost:8080/auth \
    KEYCLOAK_RESOURCE=bibmine-api \
    JNDI_NAME=jdbc/bibmine \
    DB_URL=jdbc:postgresql://bibmine-database:5432/bibmine \
    DB_USERNAME=postgres \
    DB_PASSWORD=postgres

# Run Maven
RUN mvn prepare-package -DskipTests
RUN mvn clean package -DskipTests

FROM openjdk:11-jdk
WORKDIR /bibmine-backend
COPY --from=build /bibmine-backend/target /bibmine-backend
   
EXPOSE 3000

ENTRYPOINT ["java"]
CMD ["-cp", "classes:dependency/*", "com.kumuluz.ee.EeApplication"]


