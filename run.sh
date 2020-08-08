#!/bin/bash

./gradlew clean build

docker-compose -f aeron-docker-compose.yml  up --abort-on-container-exit
docker-compose -f aeron-docker-compose.yml  down
docker-compose -f rabbitmq-docker-compose.yml  up --abort-on-container-exit
docker-compose -f rabbitmq-docker-compose.yml  down
docker-compose -f spring-boot-docker-compose.yml  up --abort-on-container-exit
docker-compose -f spring-boot-docker-compose.yml  down
./gradlew clean