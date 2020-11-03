# transport-benchmarks
measure latency for different transports with [HdrHistogram](http://hdrhistogram.org/)

## How to build

This command will create docker containers for every ping and pong components for every transport
```groovy 
./gradlew clean build
```

## How to run 

For the sake of convenience there is a bash script `run.sh` which runs all presented benchmarks.

### Aeron

Run 
```bash
docker-compose -f aeron-docker-compose.yml  up --abort-on-container-exit
```                                                                     

Clean up 
```bash  
docker-compose -f aeron-docker-compose.yml  down
```            

or you can use `run-aeron.sh` script (it will build images, run the measurement with docker and clean up the containers)

### RabbitMQ

Run 
```bash
docker-compose -f rabbitmq-docker-compose.yml  up --abort-on-container-exit
```                                                                     

Clean up 
```bash  
docker-compose -f rabbitmq-docker-compose.yml  down
```
or you can use `run-rabbitmq.sh` script (it will build images, run the measurement with docker and clean up the containers)

### Spring boot

Run 
```bash
docker-compose -f spring-boot-docker-compose.yml  up --abort-on-container-exit
```                                                                     

Clean up 
```bash  
docker-compose -f spring-boot-docker-compose.yml  down
```

or you can use `run-spring-boot.sh` script (it will build images, run the measurement with docker and clean up the containers)
