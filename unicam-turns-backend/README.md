# Unicam Turns Backend

Questo progetto Spring Boot implementa le REST API di Unicam Turns.

## Prod deployment

La dir /config (VOLUME /config in Docker) dovrà contenere:

* un file `application.yml` il cui contenuto sarà basato sul template `application-prod.yml`, con le opportune modifiche nelle parti asteriscate;
* un file `serviceAccountKey.json` con la configurazione di autenticazione per i servizi cloud di Firebase, tra cui il cloud messaging: poiché questa configurazione contiene una chiave privata, per ovvie ragioni essa non è stata aggiunta al codebase.
