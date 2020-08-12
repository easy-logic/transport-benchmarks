package io.easylogic.benchmarks.spring;

import kong.unirest.Config;
import kong.unirest.UnirestInstance;
import net.openhft.chronicle.core.jlbh.JLBH;
import net.openhft.chronicle.core.jlbh.JLBHOptions;
import net.openhft.chronicle.core.jlbh.JLBHTask;

import static io.easylogic.benchmarks.Common.*;

public class SpringBootPingBenchmarkJlbh implements JLBHTask {

    public static final String URL = SPRING_BOOT_HTTP_URL + SPRING_BOOT_PING_PONG_ENDPOINT;

    private final UnirestInstance unirest = new UnirestInstance(new Config().concurrency(1, 1));
    private JLBH jlbh;

    public static void main(String[] args) {
        JLBHOptions lth = new JLBHOptions()
                .warmUpIterations(SPRING_BOOT_WARMUP_ITERATIONS)
                .iterations(SPRING_BOOT_ITERATIONS)
                .throughput(SPRING_BOOT_THROUGHPUT)
                .runs(3)
                .jlbhTask(new SpringBootPingBenchmarkJlbh());
        new JLBH(lth).start();
    }


    @Override
    public void init(JLBH jlbh) {
        this.jlbh = jlbh;

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

    @Override
    public void run(long startTimeNS) {
        ping(startTimeNS);
    }

    private void ping(long startTimeNS) {
        unirest.post(URL)
                .field(SPRING_BOOT_TIME_PARAMETER_NAME, startTimeNS)
                .thenConsumeAsync(rawResponse -> {
                    long responseTime = Long.parseLong(rawResponse.getContentAsString());
                    jlbh.sample(System.nanoTime() - responseTime);
                });
    }

    @Override
    public void complete() {
        unirest.close();
    }

}