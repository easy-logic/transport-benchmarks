package io.easylogic.benchmarks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Common {


    public static final int AERON_WARMUP_ITERATIONS = 100_000;
    public static final int AERON_ITERATIONS = 50_000;
    public static final int AERON_THROUGHPUT = 10_000;
    public static final int RABBITMQ_WARMUP_ITERATIONS = 20_000;
    public static final int RABBITMQ_ITERATIONS = 10_000;
    public static final int RABBITMQ_THROUGHPUT = 2_000;
    public static final int SPRING_BOOT_WARMUP_ITERATIONS = 20_000;
    public static final int SPRING_BOOT_ITERATIONS = 10_000;
    public static final int SPRING_BOOT_THROUGHPUT = 2_000;

    public static final String RABBITMQ_HOST = "localhost";
    public static final String RABBITMQ_PING_QUEUE = "ping";
    public static final String RABBITMQ_PONG_QUEUE = "pong";

    public static final String SPRING_BOOT_HTTP_URL = "http://localhost:8080/";
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
