FROM openjdk:8-alpine

COPY build/libs/*.jar /md/

ENTRYPOINT java -cp /md/'*' io.aeron.driver.MediaDriver