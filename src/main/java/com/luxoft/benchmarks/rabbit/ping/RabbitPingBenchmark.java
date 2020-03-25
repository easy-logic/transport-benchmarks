package com.luxoft.benchmarks.rabbit.ping;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import net.openhft.chronicle.core.jlbh.JLBH;
import net.openhft.chronicle.core.jlbh.JLBHOptions;
import net.openhft.chronicle.core.jlbh.JLBHTask;

import java.io.IOException;
import java.nio.ByteBuffer;

public class RabbitPingBenchmark implements JLBHTask {

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Channel channel;
    private final ByteBuffer buffer = ByteBuffer.wrap(new byte[8]);

    public static void main(String[] args) {
        //Create the JLBH options you require for the benchmark
        JLBHOptions lth = new JLBHOptions()
                .warmUpIterations(100_000)
                .iterations(20000)
                .throughput(1000)
                .runs(3)
                .jlbhTask(new RabbitPingBenchmark());
        new JLBH(lth).start();
    }


    @Override
    public void init(JLBH jlbh) {
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            RabbitPingServer.addPingConsumer(channel, jlbh);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(long startTimeNS) {
        buffer.putLong(0, startTimeNS);
        try {
            channel.basicPublish("", RabbitPingServer.PING_QUEUE, null, buffer.array());
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
