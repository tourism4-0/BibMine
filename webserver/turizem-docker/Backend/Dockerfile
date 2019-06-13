FROM alpine/git as clone
WORKDIR /turizem-backend
ENV BACKEND_GIT_URL=https://XXXXXXXXXXXXXXXXXXXXXXXXXX@github.com/ajdapretnar/BibMine/webserver/turizem-backend.git
RUN git clone --branch add-webserver $BACKEND_GIT_URL

FROM maven:3.5-jdk-8-alpine as build
WORKDIR /turizem-backend
COPY --from=clone /turizem-backend/turizem-backend /turizem-backend

ENV KEYCLOAK_REALM=turizem \
    KEYCLOAK_SERVER=http://localhost:8080/auth \
    KEYCLOAK_RESOURCE=turizem-api \
    HOST_URL=localhost:3000 \
    PORT=3000 \
    JNDI_NAME=jdbc/turizem
    DB_URL=jdbc:postgresql://localhost:5432/turizem
    DB_USERNAME=postgres
    DB_PASSWORD=postgres

# Run Maven
RUN mvn prepare-package -DskipTests
RUN mvn clean package -DskipTests

FROM openjdk:8-jre-alpine
WORKDIR /turizem-backend
COPY --from=build /turizem-backend/target /turizem-backend

RUN wget https://github.com/jwilder/dockerize/releases/download/v0.6.0/dockerize-alpine-linux-amd64-v0.6.0.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-alpine-linux-amd64-v0.6.0.tar.gz \
    && rm dockerize-alpine-linux-amd64-v0.6.0.tar.gz \
    && apk update \
    && apk add openssl      

EXPOSE 3000

ENTRYPOINT ["java"]
CMD ["-Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager", "-cp", "classes:dependency/*", "com.kumuluz.ee.EeApplication"]


