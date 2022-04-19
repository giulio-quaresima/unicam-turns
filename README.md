# Unicam Turns

## Getting started

Brevi istruzioni per lanciare l'applicazione.
Prerequisiti: Java 17, Maven, Ionic CLI

### Development mode

Per lanciare la app:

```
cd unicam-turns-backend
mvn spring-boot:run &
cd ../unicam-turns-app
ionic serve
```

Per stopparla, dopo aver premuto CTRL+C per stoppare il server ionic, non dimenticare il backend che era stato lasciato in background (vedi la `&`), quindi ad esempio:

```
$ jobs
[1]+  Running                 JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 mvn spring-boot:run &
$ kill %1
... Shutdown completed.

```

### Docker test mode

Modalità per lanciare l'applicazione in ambiente Docker, che si presuppone installato nel proprio PC. In questa semplice configurazione avremo due container, uno per il DBMS e uno per il _backend_ della nostra applicazione. Compiliamo anzitutto l'immagine Docker del nostro backend:

```
cd unicam-turns-backend
docker build -t unicam/unicam-turns-backend .
```

Quindi prepariamo un file di configurazione `application.yml` con il seguente contenuto:

```
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/postgres
    username: postgres
    password: changeme
```

E mettiamolo in una cartella di nostra scelta, io ad esempio l'ho messo in `/home/giulio/var/docker/volumes/unicam/turns/unicam-turns-backend/config`.

Ora occorre creare una rete Docker con cui far comunicare privatamente i _container_ (se si ha già una rete e si vuole usare questa si può saltare questo step); io ne creerò una chiamata `unicam-turns`:

```
docker network create unicam-turns
```
A questo punto lanciamo il container del DBMS:

```
docker run --name unicam-turns-postgres -e POSTGRES_PASSWORD=changeme --network unicam-turns --network-alias postgres -d postgres
```

e il container del nostro _backend_:

```
docker run -p 8080:8080 --network unicam-turns -v /home/giulio/var/docker/volumes/unicam/turns/unicam-turns-backend/config:/config unicam/unicam-turns-backend
```

A questo punto, se tutto è andato a buon fine, aprendo il browser all'indirizzo http://localhost:8080/ si dovrebbe vedere un JSON con alcune informazioni. Per lanciare la app, si può procedere come nel "Development mode", ovvero:

```
cd ../unicam-turns-app
ionic serve
```


