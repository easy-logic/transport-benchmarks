package io.easylogic.benchmarks;

import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import org.agrona.concurrent.BusySpinIdleStrategy;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SigInt;
import org.agrona.concurrent.UnsafeBuffer;

import java.util.concurrent.atomic.AtomicBoolean;

import static io.aeron.samples.SampleConfiguration.*;

public class AeronPong {

    public static void main(String[] args) {
        AtomicBoolean isRunning = new AtomicBoolean(true);
        SigInt.register(() -> {
            System.out.println("SIGINT signal");
            isRunning.set(false);
        });

        System.out.println("Connect to Aeron...");
        try (Aeron aeron = Aeron.connect();
             Subscription pingSubscription = aeron.addSubscription(PING_CHANNEL, PING_STREAM_ID);
             Publication pongPublication = aeron.addExclusivePublication(PONG_CHANNEL, PONG_STREAM_ID)) {

            IdleStrategy idleStrategy = new BusySpinIdleStrategy();
            UnsafeBuffer buffer = new UnsafeBuffer(new byte[MESSAGE_LENGTH]);
            FragmentHandler fragmentHandler = (message, offset, length, header) -> {
                long time = message.getLong(offset);
                buffer.putLong(0, time);
                while (pongPublication.offer(buffer) < 0) {
                    idleStrategy.idle();
                }
            };

            System.out.println("Start Pong");
            while (isRunning.get()) {
                idleStrategy.idle(pingSubscription.poll(fragmentHandler, FRAGMENT_COUNT_LIMIT));
            }
            System.out.println("Stopping pong...");

            System.out.println("Pong has been stopped");
        }
    }
}
