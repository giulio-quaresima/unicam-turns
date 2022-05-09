#!/bin/sh

mvn clean package
docker build -t unicam/unicam-turns-backend .
docker save unicam/unicam-turns-backend | gzip > target/unicam-turns-backend.docker.tar.gz

