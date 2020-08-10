FROM openjdk:8-alpine

COPY build/libs/*.jar /md/

ENTRYPOINT java -Daeron.threading.mode=DEDICATED -Daeron.sender.idle.strategy=noop -Daeron.receiver.idle.strategy=noop -cp /md/'*' io.aeron.driver.MediaDriver