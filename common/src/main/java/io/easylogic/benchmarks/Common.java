package io.easylogic.benchmarks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Common {


    public static final int AERON_WARMUP_ITERATIONS = 20_000;
    public static final int AERON_ITERATIONS = 10_000;
    public static final int AERON_THROUGHPUT = 2_000;
    public static final int RABBITMQ_WARMUP_ITERATIONS = 20_000;
    public static final int RABBITMQ_ITERATIONS = 10_000;
    public static final int RABBITMQ_THROUGHPUT = 2_000;
    public static final int SPRING_BOOT_WARMUP_ITERATIONS = 20_000;
    public static final int SPRING_BOOT_ITERATIONS = 10_000;
    public static final int SPRING_BOOT_THROUGHPUT = 2_000;

    public static final String PING_CHANNEL = "aeron:udp?endpoint=224.0.1.1:40457";
    public static final String PONG_CHANNEL = "aeron:udp?endpoint=224.0.1.1:40457";
    public static final int PING_STREAM_ID = 1002;
    public static final int PONG_STREAM_ID = 1003;
    public static final int FRAGMENT_COUNT_LIMIT = 10;
    public static final int MESSAGE_LENGTH = 32;



    public static final String RABBITMQ_HOST = "localhost";
    public static final String RABBITMQ_PING_QUEUE = "ping";
    public static final String RABBITMQ_PONG_QUEUE = "pong";

    public static final String SPRING_BOOT_HTTP_URL = "http://localhost:9999/";
    public static final String SPRING_BOOT_PING_PONG_ENDPOINT = "/ping-pong";
    public static final String SPRING_BOOT_TIME_PARAMETER_NAME = "time";


    public static <T> T retryForTimeout(int timeout, TimeUnit timeUnit, ExceptionSupplier<T> action) throws TimeoutException {
        long startTime = System.nanoTime();
        boolean fail = true;
        T result = null;
        do {
            try {
                result = action.run();
                fail = false;
            } catch (Exception e) {
                if (System.nanoTime() - startTime > timeUnit.toNanos(timeout)) {
                    throw new TimeoutException(e.getMessage());
                }
            }
        } while (fail);

        return result;
    }

    @FunctionalInterface
    public interface ExceptionSupplier<T> {

        T run() throws Exception;

    }

}
