package io.easylogic.benchmarks.rabbit;

import com.google.common.primitives.Longs;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import io.easylogic.benchmarks.Common;
import org.HdrHistogram.Histogram;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.easylogic.benchmarks.Common.*;

public class RabbitPingBenchmarkHdrHistogram implements AutoCloseable{
    private static final Histogram HISTOGRAM = new Histogram(TimeUnit.SECONDS.toNanos(10), 3);

    private final Connection connection;
    private final Channel channel;
    private final ByteBuffer buffer = ByteBuffer.wrap(new byte[8]);
    private final AtomicBoolean receivedMessage = new AtomicBoolean(false);

    public static void main(String[] args) throws TimeoutException {
        try (RabbitPingBenchmarkHdrHistogram benchmark = new RabbitPingBenchmarkHdrHistogram()) {
            System.out.format("Warming up... %,d messages%n", Common.RABBITMQ_WARMUP_ITERATIONS);
            for (int i = 0; i < RABBITMQ_WARMUP_ITERATIONS; i++) {
                benchmark.ping();
            }

            synchronized (HISTOGRAM) {
                HISTOGRAM.reset();
            }

            System.out.format("Pinging %,d messages%n", RABBITMQ_ITERATIONS);
            for (int i = 0; i < RABBITMQ_ITERATIONS; i++) {
                benchmark.ping();
            }
            
            synchronized (HISTOGRAM) {
                System.out.println("Histogram of RTT latencies in microseconds.");
                HISTOGRAM.outputPercentileDistribution(System.out, 1000.0);
            }
        }
    }

    public RabbitPingBenchmarkHdrHistogram() throws TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setConnectionTimeout(2000);
        connectionFactory.setHost(RABBITMQ_HOST);
        System.out.println("Wait for RabbitMQ...");

        connection = Common.retryForTimeout(10, TimeUnit.SECONDS, connectionFactory::newConnection);
        try {
            channel = connection.createChannel();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            addPongConsumer(channel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void addPongConsumer(Channel channel) throws IOException {
        channel.queueDeclare(RABBITMQ_PONG_QUEUE, false, true, true, null);
        DeliverCallback callback = (consumerTag, message) -> {
            long rtt = System.nanoTime() - Longs.fromByteArray(message.getBody());
            synchronized (HISTOGRAM) {
                HISTOGRAM.recordValue(rtt);
            }
            receivedMessage.set(true);
        };
        channel.basicConsume(RABBITMQ_PONG_QUEUE, true, callback, consumerTag -> {
        });
    }

    public void ping() {
        buffer.putLong(0, System.nanoTime());
        try {
            channel.basicPublish("", RABBITMQ_PING_QUEUE, null, buffer.array());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (!receivedMessage.get()) {
            Thread.yield();
        }
        receivedMessage.set(false);
    }

    public void close() {
        try {
            channel.close();
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
