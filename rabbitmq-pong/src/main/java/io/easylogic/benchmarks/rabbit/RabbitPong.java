package io.easylogic.benchmarks.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import io.easylogic.benchmarks.Common;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static io.easylogic.benchmarks.Common.*;

public class RabbitPong {

    public static void main(String[] args) throws TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(RABBITMQ_HOST);
        connectionFactory.setConnectionTimeout(10000);
        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setHandshakeTimeout(10000);
        Connection connection = Common.retryForTimeout(10, TimeUnit.SECONDS, connectionFactory::newConnection);
        try {
            Channel channel = connection.createChannel();

            addPingConsumer(channel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static void addPingConsumer(Channel channel) throws IOException {

        channel.queueDeclare(RABBITMQ_PING_QUEUE, false, true, true, null);
        DeliverCallback callback = (consumerTag, message) -> {
            channel.basicPublish("", RABBITMQ_PONG_QUEUE, null, message.getBody());
        };
        channel.basicConsume(RABBITMQ_PING_QUEUE, true, callback, consumerTag -> {
        });
    }

}
