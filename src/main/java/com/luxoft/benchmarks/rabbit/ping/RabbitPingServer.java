package com.luxoft.benchmarks.rabbit.ping;

import com.google.common.primitives.Longs;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import net.openhft.chronicle.core.jlbh.JLBH;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeoutException;

public class RabbitPingServer {

    public static final String PING_QUEUE = "ping";

    public static void addPingConsumer(Channel channel, JLBH jlbh) throws IOException {

        channel.queueDeclare(PING_QUEUE, false, true, true, null);
        DeliverCallback callback = (consumerTag, message) -> {
            jlbh.sample(System.nanoTime() - Longs.fromByteArray(message.getBody()));
        };
        channel.basicConsume(PING_QUEUE, true, callback, consumerTag -> {});
    }

}
