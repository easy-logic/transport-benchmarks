#!/bin/bash

./gradlew spring-boot-ping:clean spring-boot-pong:clean spring-boot-ping:build spring-boot-pong:build

docker-compose -f spring-boot-docker-compose.yml  up --abort-on-container-exit
docker-compose -f spring-boot-docker-compose.yml  down

./gradlew spring-boot-ping:clean spring-boot-pong:clean