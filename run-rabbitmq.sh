#!/bin/bash

./gradlew rabbitmq-ping:clean rabbitmq-pong:clean rabbitmq-ping:build rabbitmq-pong:build

docker-compose -f rabbitmq-docker-compose.yml  up --abort-on-container-exit
docker-compose -f rabbitmq-docker-compose.yml  down

./gradlew rabbitmq-ping:clean rabbitmq-pong:clean