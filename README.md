# Unicam Turns


## Getting started

Brevi istruzioni per lanciare l'applicazione.
Prerequisiti: Java 17, Maven, Ionic CLI

### Development mode

A causa di alcune limitazioni all'uso di localhost con `spring-authorization-server`
è necessario evitare l'utilizzo di `localhost` come hostname per il client
e l'authorization server. Per questo, ho aggiunto la seguente configurazione
nel file `/etc/hosts`

```
127.0.0.1	unicam-turns-app
127.0.0.1	unicam-turns-authorization-server
127.0.0.1	unicam-turns-backend
```

(questo funziona in Linux, non ho idea se e come fare in altri OS)

Per lanciare quindi il tutto:

```
cd unicam-turns-authorization-server
mvn spring-boot:run &
cd ../unicam-turns-backend
mvn spring-boot:run &
cd ../unicam-turns-app
ionic serve --host=unicam-turns-app
```

Per stopparla, dopo aver premuto CTRL+C per stoppare il server ionic, non dimenticare i backend 
che erano stati lasciati in background (vedi la `&`), quindi ad esempio:

```
$ jobs
[1]+  Running                 JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 mvn spring-boot:run &
$ kill %1
... Shutdown completed.

```
### SSL mode
Dopo [#10](https://github.com/giulio-quaresima/unicam-turns/issues/10) bisogna fare le configurazioni opportune (vedi [#10](https://github.com/giulio-quaresima/unicam-turns/issues/10)) e lanciare la app aggiungendo l'opzione `--ssl`:
```
ionic serve --ssl --host=unicam-turns-app
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


