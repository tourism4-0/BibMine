# BibMine Backend

This is BibMine backend, whose purpose is to fetch data from the SCOPUS database and store it in a separate database.  

Requirements to run the backend:
  - Ubuntu Server
  - Java OpenJDK 11
  - Maven
  - Keycloak 5.0
  - PostgreSQL
  
If you would like to run the BibMine service and interact with it externally, it is necessary for you to register a domain to your IP and setup letsencrypt service. Otherwise, if you run and interact with BibMine locally or security is not your priority you can turn off the ssl property in Keycloak.  
  
## Keycloak setup
Setup
```
cd ~/
wget https://downloads.jboss.org/keycloak/5.0.0/keycloak-5.0.0.tar.gz
tar -xvzf keycloak-5.0.0.tar.gz
sudo rm keycloak-5.0.0.tar.gz
```
Import the preset configuration when running Keycloak for the first time.

*NOTE: Make sure to choose the correct location of the keycloak-configuration.json when running the command bellow.*
```
./keycloak-5.0.0/bin/standalone.sh -Dkeycloak.migration.action=import -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.file=<BibMine webserver location>/Keycloak/keycloak-configuration.json -Dkeycloak.migration.strategy=OVERWRITE_EXISTING

```
After the first start, in the future Keycloak can be started by simply running:
```
./keycloak-5.0.0/bin/standalone.sh
```
Now the Keycloak server can be accessed through http://localhost:800/auth/

Create users and assign roles. By default there is one user created for testing purposes.

## Database setup
Setup PostgreSQL
```
sudo apt-get install postgresql
sudo -u postgres psql postgres
\password
\q
```
Create the database
```
sudo -u postgres createdb <dbname>
sudo -u postgres psql -U postgres -a -f ~/<BibMine webserver location>/bibmine-database/01_db.sql
```
## KumuluzEE backend
Open run-local.sh script that is blaced in <BibMine webserver location>/bibmine-backend and set the environment variables for the backend.

Run the script ./run-local.sh

## Docker
Setup Docker
```
sudo apt-get update
sudo apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    software-properties-common
    
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"
    
sudo apt-get install docker-ce
sudo curl -L "https://github.com/docker/compose/releases/download/1.22.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```
Check if Docker works
```
sudo docker run hello-world
```
Open bibmine.env from <BibMine webserver location>/bibmine-docker and Dockerfile from <BibMine webserver location>/bibmine-docker/Backend to set the environment variables.
  
Build and run the containers
```
docker-compose -f docker-compose.yml up --build -d
```
To stop the services and remove their images run the following
```
docker-compose -f docker-compose.yml down --rmi all
```

## Accessing the APIs from external servers
In order to access the APIs it is necessary first to receive authorization token from Keycloak by running the following request: POST <server IP>/auth/realms/bibmine/protocol/openid-connect/token. All necessary body parameters for this request can be found in the Postman collection located in <BibMine webserver location>/bibmine-postman.
  
After receiving the token from Keycloak, it should be put as Authorization header in all the requests for operations over the articles and article queries on the BibMine server.   
  

  
