package io.easylogic.benchmarks;

import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import net.openhft.chronicle.core.jlbh.JLBH;
import net.openhft.chronicle.core.jlbh.JLBHOptions;
import net.openhft.chronicle.core.jlbh.JLBHTask;
import org.agrona.CloseHelper;
import org.agrona.collections.MutableBoolean;
import org.agrona.concurrent.BusySpinIdleStrategy;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingMillisIdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;

import static io.aeron.samples.SampleConfiguration.*;

public class AeronPingBenchmarkJlbh implements JLBHTask {
    private final IdleStrategy idleStrategy = new BusySpinIdleStrategy();
    private final MutableBoolean receivedMessage = new MutableBoolean(false);
    private final Aeron aeron;
    private final Publication pingPublication;
    private final Subscription pongSubscription;
    private final UnsafeBuffer buffer;
    private final FragmentHandler fragmentHandler;
    private JLBH jlbh;

    public static void main(String[] args) {
        //Create the JLBH options you require for the benchmark
        JLBHOptions lth = new JLBHOptions()
                .warmUpIterations(Common.AERON_WARMUP_ITERATIONS)
                .iterations(Common.AERON_ITERATIONS)
                .throughput(Common.AERON_THROUGHPUT)
                .runs(3)
                .recordOSJitter(true)
                .jlbhTask(new AeronPingBenchmarkJlbh());
        new JLBH(lth).start();
    }

    public AeronPingBenchmarkJlbh() {
        aeron = Aeron.connect();
        pingPublication = aeron.addExclusivePublication(PING_CHANNEL, PING_STREAM_ID);
        pongSubscription = aeron.addSubscription(PONG_CHANNEL, PONG_STREAM_ID);
        buffer = new UnsafeBuffer(new byte[MESSAGE_LENGTH]);
        fragmentHandler = (buffer, offset, length, header) -> {
            long sendTime = buffer.getLong(offset);
            jlbh.sample(System.nanoTime() - sendTime);
            receivedMessage.set(true);
        };
    }

    @Override
    public void init(JLBH jlbh) {
        this.jlbh = jlbh;
        IdleStrategy idleStrategy = new SleepingMillisIdleStrategy(1);
        while (!pingPublication.isConnected() || pongSubscription.hasNoImages()) {
            idleStrategy.idle();
        }
    }

    @Override
    public void run(long startTimeNS) {
        buffer.putLong(0, startTimeNS);
        while (pingPublication.offer(buffer) < 0) {
            idleStrategy.idle();
        }

        while (!receivedMessage.get()) {
            idleStrategy.idle(pongSubscription.poll(fragmentHandler, FRAGMENT_COUNT_LIMIT));
        }

        receivedMessage.set(false);
    }

    @Override
    public void complete() {
        CloseHelper.closeAll(pongSubscription, pingPublication, aeron);
    }
}
