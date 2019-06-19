FROM alpine/git as clone
WORKDIR /git_repo
ENV GIT_URL=https://{YOUR_TOKEN}:@github.com/ajdapretnar/BibMine.git\
    KEYCLOAK_URL=https://downloads.jboss.org/keycloak/5.0.0/keycloak-5.0.0.tar.gz
RUN git clone --branch add_webserver $GIT_URL

WORKDIR /turizem-keycloak
RUN wget $KEYCLOAK_URL \
 && tar -xvzf keycloak-5.0.0.tar.gz \
 && rm /turizem-keycloak/keycloak-5.0.0.tar.gz \
 && cp -r /git_repo/BibMine/webserver/turizem-keycloak/* /turizem-keycloak \ 
 && rm -rf /git_repo 
 
FROM openjdk:8-jre-alpine as build
WORKDIR /turizem-keycloak
COPY --from=clone /turizem-keycloak/keycloak-5.0.0 /turizem-keycloak/keycloak-5.0.0
COPY --from=clone /turizem-keycloak/keycloak-configuration.json /turizem-keycloak/keycloak-5.0.0/

EXPOSE 8080
CMD ["./keycloak-5.0.0/bin/standalone.sh", "-b", "0.0.0.0", "-Dkeycloak.migration.action=import", "-Dkeycloak.migration.provider=singleFile", "-Dkeycloak.migration.file=keycloak-5.0.0/keycloak-configuration.json", "-Dkeycloak.migration.strategy=IGNORE_EXISTING"]
