package io.easylogic.benchmarks.rabbit;

import com.google.common.primitives.Longs;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import io.easylogic.benchmarks.Common;
import net.openhft.chronicle.core.jlbh.JLBH;
import net.openhft.chronicle.core.jlbh.JLBHOptions;
import net.openhft.chronicle.core.jlbh.JLBHTask;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static io.easylogic.benchmarks.Common.*;

public class RabbitPingBenchmark implements JLBHTask {


    private final Connection connection;
    private final Channel channel;
    private final ByteBuffer buffer = ByteBuffer.wrap(new byte[8]);

    public static void main(String[] args) throws TimeoutException {
        //Create the JLBH options you require for the benchmark
        JLBHOptions lth = new JLBHOptions()
                .warmUpIterations(100_000)
                .iterations(10000)
                .throughput(2000)
                .runs(3)
                .jlbhTask(new RabbitPingBenchmark());
        new JLBH(lth).start();
    }

    public RabbitPingBenchmark() throws TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setConnectionTimeout(2000);
        connectionFactory.setHost(RABBITMQ_HOST);
        connection = Common.retryForTimeout(10, TimeUnit.SECONDS, connectionFactory::newConnection);
        try {
            channel = connection.createChannel();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init(JLBH jlbh) {
        try {
            addPongConsumer(channel, jlbh);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void addPongConsumer(Channel channel, JLBH jlbh) throws IOException {
        channel.queueDeclare(RABBITMQ_PONG_QUEUE, false, true, true, null);
        DeliverCallback callback = (consumerTag, message) -> {
            jlbh.sample(System.nanoTime() - Longs.fromByteArray(message.getBody()));
        };
        channel.basicConsume(RABBITMQ_PONG_QUEUE, true, callback, consumerTag -> {
        });
    }

    @Override
    public void run(long startTimeNS) {
        buffer.putLong(0, startTimeNS);
        try {
            channel.basicPublish("", RABBITMQ_PING_QUEUE, null, buffer.array());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void complete() {
        try {
            channel.close();
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
