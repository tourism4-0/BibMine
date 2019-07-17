FROM alpine/git as clone
WORKDIR /git_repo
ENV GIT_URL=https://github.com/ajdapretnar/BibMine.git\
    KEYCLOAK_URL=https://downloads.jboss.org/keycloak/5.0.0/keycloak-5.0.0.tar.gz
RUN git clone --branch add_webserver $GIT_URL

WORKDIR /bibmine-keycloak
RUN wget $KEYCLOAK_URL \
 && tar -xvzf keycloak-5.0.0.tar.gz \
 && rm /bibmine-keycloak/keycloak-5.0.0.tar.gz \
 && cp -r /git_repo/BibMine/webserver/bibmine-keycloak/* /bibmine-keycloak \ 
 && rm -rf /git_repo 
 
FROM openjdk:8-jre-alpine as build
WORKDIR /bibmine-keycloak
COPY --from=clone /bibmine-keycloak/keycloak-5.0.0 /bibmine-keycloak/keycloak-5.0.0
COPY --from=clone /bibmine-keycloak/standalone.xml /bibmine-keycloak/keycloak-5.0.0/standalone/configuration/
COPY --from=clone /bibmine-keycloak/keycloak-configuration.json /bibmine-keycloak/keycloak-5.0.0/

EXPOSE 8080
CMD ["./keycloak-5.0.0/bin/standalone.sh", "-b", "0.0.0.0", "-Dkeycloak.migration.action=import", "-Dkeycloak.migration.provider=singleFile", "-Dkeycloak.migration.file=keycloak-5.0.0/keycloak-configuration.json", "-Dkeycloak.migration.strategy=IGNORE_EXISTING"]
