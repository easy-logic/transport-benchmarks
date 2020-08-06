package io.easylogic.benchmarks.spring;

import io.easylogic.benchmarks.Common;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import net.openhft.chronicle.core.jlbh.JLBH;
import net.openhft.chronicle.core.jlbh.JLBHOptions;
import net.openhft.chronicle.core.jlbh.JLBHTask;

public class SpringBootPingBenchmark implements JLBHTask {

    public static final String URL = Common.SPRING_BOOT_HTTP_URL + Common.SPRING_BOOT_PING_PONG_ENDPOINT;

    private JLBH jlbh;

    public static void main(String[] args) {
        //Create the JLBH options you require for the benchmark                     Rabb
        JLBHOptions lth = new JLBHOptions()
                .warmUpIterations(50_000)
                .iterations(10000)
                .throughput(2000)
                .runs(3)
                .jlbhTask(new SpringBootPingBenchmark());
        new JLBH(lth).start();
    }


    @Override
    public void init(JLBH jlbh) {
        this.jlbh = jlbh;

        System.out.println("Wait for server...");
        boolean noResponse = true;
        do {
            try {
                HttpResponse<Long> ping = ping(0);
                noResponse = false;
            } catch (Exception e) {

            }
        } while (noResponse);
        System.out.println("Server is up");
    }

    @Override
    public void run(long startTimeNS) {
        HttpResponse<Long> ping = ping(startTimeNS);

        jlbh.sample(System.nanoTime() - ping.getBody());
    }

    private HttpResponse<Long> ping(long startTimeNS) {
        return Unirest.post(URL)
                .field(Common.SPRING_BOOT_TIME_PARAMETER_NAME, startTimeNS)
                .asObject(Long.class);
    }

    @Override
    public void complete() {
    }
}