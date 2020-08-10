#!/bin/bash

./gradlew aeron-md:clean aeron-ping:clean aeron-pong:clean aeron-md:build aeron-ping:build aeron-pong:build

docker-compose -f aeron-docker-compose.yml  up --abort-on-container-exit
docker-compose -f aeron-docker-compose.yml  down

./gradlew aeron-md:clean aeron-ping:clean aeron-pong:clean 