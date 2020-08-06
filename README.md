# transport-benchmarks
measure latency for different transports with jlbh 

## How to build

This command will create docker containers for every ping and pong components for every transport
```groovy 
./gradlew clean build
```

## How to run 

### Aeron

Run 
```bash
docker-compose -f aeron-docker-compose.yml  up --abort-on-container-exit
```                                                                     

Clean up 
```bash  
docker-compose -f aeron-docker-compose.yml  down
```

### RabbitMQ

Run 
```bash
docker-compose -f rabbitmq-docker-compose.yml  up --abort-on-container-exit
```                                                                     

Clean up 
```bash  
docker-compose -f rabbitmq-docker-compose.yml  down
```

### Spring boot

Run 
```bash
docker-compose -f spring-boot-docker-compose.yml  up --abort-on-container-exit
```                                                                     

Clean up 
```bash  
docker-compose -f spring-boot-docker-compose.yml  down
```

