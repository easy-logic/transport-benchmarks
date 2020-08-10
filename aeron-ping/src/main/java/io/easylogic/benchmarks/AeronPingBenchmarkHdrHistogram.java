package io.easylogic.benchmarks;

import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import org.HdrHistogram.Histogram;
import org.agrona.CloseHelper;
import org.agrona.collections.MutableBoolean;
import org.agrona.concurrent.BusySpinIdleStrategy;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingMillisIdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;

import java.util.concurrent.TimeUnit;

import static io.aeron.samples.SampleConfiguration.*;

public class AeronPingBenchmarkHdrHistogram implements AutoCloseable {
    private static final Histogram HISTOGRAM = new Histogram(TimeUnit.SECONDS.toNanos(10), 3);

    private final IdleStrategy idleStrategy = new BusySpinIdleStrategy();
    private final MutableBoolean receivedMessage = new MutableBoolean(false);
    private final Aeron aeron;
    private final Publication pingPublication;
    private final Subscription pongSubscription;
    private final UnsafeBuffer buffer;
    private final FragmentHandler fragmentHandler;

    public static void main(final String[] args) throws Exception {
        try (AeronPingBenchmarkHdrHistogram benchmark = new AeronPingBenchmarkHdrHistogram()) {
            System.out.format("Warming up... %,d messages%n", Common.AERON_WARMUP_ITERATIONS);
            for (int i = 0; i < Common.AERON_WARMUP_ITERATIONS; i++) {
                benchmark.ping();
            }

            HISTOGRAM.reset();

            System.out.format("Pinging %,d messages%n", Common.AERON_ITERATIONS);
            for (int i = 0; i < Common.AERON_ITERATIONS; i++) {
                benchmark.ping();
            }

            System.out.println("Histogram of RTT latencies in microseconds.");
            HISTOGRAM.outputPercentileDistribution(System.out, 1000.0);
        }
    }

    public AeronPingBenchmarkHdrHistogram() {
        aeron = Aeron.connect();
        pingPublication = aeron.addExclusivePublication(PING_CHANNEL, PING_STREAM_ID);
        pongSubscription = aeron.addSubscription(PONG_CHANNEL, PONG_STREAM_ID);
        IdleStrategy idleStrategy = new SleepingMillisIdleStrategy(1);
        System.out.println("Wait for Pong...");
        while (!pingPublication.isConnected() || pongSubscription.hasNoImages()) {
            idleStrategy.idle();
        }

        fragmentHandler = (buffer, offset, length, header) -> {
            long sendTime = buffer.getLong(offset);
            final long rttNs = System.nanoTime() - sendTime;

            HISTOGRAM.recordValue(rttNs);
            receivedMessage.set(true);
        };

        buffer = new UnsafeBuffer(new byte[MESSAGE_LENGTH]);
    }


    public void ping() {
        buffer.putLong(0, System.nanoTime());
        while (pingPublication.offer(buffer) < 0) {
            idleStrategy.idle();
        }

        while (!receivedMessage.get()) {
            idleStrategy.idle(pongSubscription.poll(fragmentHandler, FRAGMENT_COUNT_LIMIT));
        }

        receivedMessage.set(false);
    }

    @Override
    public void close() throws Exception {
        CloseHelper.closeAll(pongSubscription, pingPublication, aeron);
    }
}
