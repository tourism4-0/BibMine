FROM alpine/git as clone
WORKDIR /turizem-keycloak
ENV PM_GIT_URL=https://XXXXXXXXXXXXXXXXXX@github.com/ajdapretnar/BibMine/webserver/turizem-backend.git
 \
    KEYCLOAK_URL=https://downloads.jboss.org/keycloak/5.0.0/keycloak-5.0.0.tar.gz

RUN wget $KEYCLOAK_URL \
 && tar -xvzf keycloak-5.0.0.tar.gz \
 && git clone $PM_GIT_URL \
 && rm /turizem-keycloak/keycloak-5.0.0.Final.tar.gz \
 
FROM openjdk:8-jre-alpine as build
WORKDIR /turizem-keycloak
COPY --from=clone /turizem-keycloak/keycloak-5.0.0 /turizem-keycloak/keycloak-5.0.0
COPY --from=clone /turizem-keycloak/turizem-keycloak/keycloak-configuration.json /turizem-keycloak/keycloak-5.0.0/

EXPOSE 8080
CMD ["./keycloak-5.0.0/bin/standalone.sh", "-b", "0.0.0.0", "-Dkeycloak.migration.action=import", "-Dkeycloak.migration.provider=singleFile", "-Dkeycloak.migration.file=keycloak-5.0.0/keycloak-configuration.json", "-Dkeycloak.migration.strategy=IGNORE_EXISTING"]
