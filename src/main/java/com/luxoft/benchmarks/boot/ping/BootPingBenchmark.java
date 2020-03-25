package com.luxoft.benchmarks.boot.ping;

import com.luxoft.benchmarks.boot.SpringBootApplication;
import kong.unirest.Unirest;
import net.openhft.chronicle.core.jlbh.JLBH;
import net.openhft.chronicle.core.jlbh.JLBHOptions;
import net.openhft.chronicle.core.jlbh.JLBHTask;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class BootPingBenchmark implements JLBHTask {

    public static final String URL = "http://localhost:8080/" + BootPingServer.PING_ENDPOINT;

    private ConfigurableApplicationContext app;

    public static void main(String[] args) {
        //Create the JLBH options you require for the benchmark
        JLBHOptions lth = new JLBHOptions()
                .warmUpIterations(100_000)
                .iterations(20000)
                .throughput(1000)
                .runs(3)
                .jlbhTask(new BootPingBenchmark());
        new JLBH(lth).start();
    }


    @Override
    public void init(JLBH jlbh) {
        app = SpringApplication.run(SpringBootApplication.class);
        BootPingServer pingServer = app.getBean(BootPingServer.class);
        pingServer.setJlbh(jlbh);
    }

    @Override
    public void run(long startTimeNS) {
        Unirest.post(URL)
                .field(BootPingServer.TIME_PARAMETER_NAME, startTimeNS)
                .asEmpty();
    }

    @Override
    public void complete() {
        app.close();
    }
}
