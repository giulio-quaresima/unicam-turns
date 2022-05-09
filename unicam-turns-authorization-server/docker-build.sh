#!/bin/sh

mvn clean package
docker build -t unicam/unicam-turns-authorization-server .
docker save unicam/unicam-turns-authorization-server | gzip > target/unicam-turns-authorization-server.docker.tar.gz

