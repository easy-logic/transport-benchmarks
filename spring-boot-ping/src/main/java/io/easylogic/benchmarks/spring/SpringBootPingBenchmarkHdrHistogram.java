package io.easylogic.benchmarks.spring;

import io.easylogic.benchmarks.Common;
import kong.unirest.Config;
import kong.unirest.UnirestInstance;
import org.HdrHistogram.Histogram;

import java.util.concurrent.TimeUnit;

import static io.easylogic.benchmarks.Common.SPRING_BOOT_ITERATIONS;
import static io.easylogic.benchmarks.Common.SPRING_BOOT_WARMUP_ITERATIONS;

public class SpringBootPingBenchmarkHdrHistogram implements AutoCloseable {
    private static final Histogram HISTOGRAM = new Histogram(TimeUnit.SECONDS.toNanos(10), 3);
    public static final String URL = Common.SPRING_BOOT_HTTP_URL + Common.SPRING_BOOT_PING_PONG_ENDPOINT;

    private final UnirestInstance unirest = new UnirestInstance(new Config().concurrency(1, 1));

    public static void main(String[] args) {
        try (SpringBootPingBenchmarkHdrHistogram benchmark = new SpringBootPingBenchmarkHdrHistogram()) {
            System.out.format("Warming up... %,d messages%n", Common.SPRING_BOOT_WARMUP_ITERATIONS);
            for (int i = 0; i < SPRING_BOOT_WARMUP_ITERATIONS; i++) {
                benchmark.ping();
            }

            HISTOGRAM.reset();

            System.out.format("Pinging %,d messages%n", SPRING_BOOT_ITERATIONS);
            for (int i = 0; i < SPRING_BOOT_ITERATIONS; i++) {
                benchmark.ping();
            }

            System.out.println("Histogram of RTT latencies in microseconds.");
            HISTOGRAM.outputPercentileDistribution(System.out, 1000.0);
        }
    }

    public SpringBootPingBenchmarkHdrHistogram() {
        System.out.println("Wait for server...");
        boolean noResponse = true;
        do {
            try {
                unirest.post(URL).asEmpty();
                noResponse = false;
            } catch (Exception e) {

            }
        } while (noResponse);
        System.out.println("Server is up");
    }

    public void ping() {
        unirest.post(URL)
                .field(Common.SPRING_BOOT_TIME_PARAMETER_NAME, System.nanoTime())
                .thenConsume(rawResponse -> {
                    long responseTime = Long.parseLong(rawResponse.getContentAsString());
                    HISTOGRAM.recordValue(System.nanoTime() - responseTime);
                });
    }

    public void close() {
        try {
            unirest.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
