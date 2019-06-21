#!/bin/bash
# Generate keycloak.json file with maven replacer plugin based on Keycloak configuration settings

export KEYCLOAK_REALM=turizem
export KEYCLOAK_SERVER=http://localhost:8080/auth
export KEYCLOAK_RESOURCE=turizem-api

# Postgres configuration
export JNDI_NAME=jdbc/turizem
export DB_URL=jdbc:postgresql://localhost:5432/turizem
export DB_USERNAME=postgres
export DB_PASSWORD=postgres

export HOST_URL=localhost:3000

mvn prepare-package -DskipTests
mvn clean package -DskipTests

# Set environmental variables
# Jetty port
export SCOPUS_KEY=ad751272c5fa07ea01537f133b200940
export PORT=3000

# Task timer period [s]
export timerPeriod=86400000

# Start application
java -cp target/classes:target/dependency/* com.kumuluz.ee.EeApplication

