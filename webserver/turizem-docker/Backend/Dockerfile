FROM alpine/git as clone
WORKDIR /git_repo

ENV GIT_URL=https://{YOUR_TOKEN}:@github.com/ajdapretnar/BibMine.git
RUN git clone --branch add_webserver $GIT_URL 

WORKDIR /turizem-backend
RUN cp -r /git_repo/BibMine/webserver/turizem-backend/* /turizem-backend 
RUN rm -rf /git_repo 

FROM maven:3.5-jdk-8-alpine as build
WORKDIR /turizem-backend
COPY --from=clone /turizem-backend /turizem-backend

ENV HOST_URL=0.0.0.0 \
    PORT=3000 \
	KEYCLOAK_REALM=turizem \
    KEYCLOAK_SERVER=http://turizem-keycloak:8080/auth \
    KEYCLOAK_RESOURCE=turizem-api \
    JNDI_NAME=jdbc/turizem \
    DB_URL=jdbc:postgresql://turizem-database:5432/turizem \
    DB_USERNAME=postgres \
    DB_PASSWORD=postgres

# Run Maven
RUN mvn prepare-package -DskipTests
RUN mvn clean package -DskipTests

FROM openjdk:11-jdk
WORKDIR /turizem-backend
COPY --from=build /turizem-backend/target /turizem-backend
   
EXPOSE 3000

ENTRYPOINT ["java"]
CMD ["-cp", "classes:dependency/*", "com.kumuluz.ee.EeApplication"]


