package io.easylogic.benchmarks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Common {

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
