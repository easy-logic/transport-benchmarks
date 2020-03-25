package com.luxoft.benchmarks.boot.ping_pong;

import com.luxoft.benchmarks.boot.SpringBootApplication;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import net.openhft.chronicle.core.jlbh.JLBH;
import net.openhft.chronicle.core.jlbh.JLBHOptions;
import net.openhft.chronicle.core.jlbh.JLBHTask;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class BootPingPongBenchmark implements JLBHTask {

    public static final String URL = "http://localhost:8080/" + BootPingPongServer.PING_PONG_ENDPOINT;

    private ConfigurableApplicationContext app;
    private JLBH jlbh;

    public static void main(String[] args) {
        //Create the JLBH options you require for the benchmark
        JLBHOptions lth = new JLBHOptions()
                .warmUpIterations(100_000)
                .iterations(2000)
                .throughput(1000)
                .runs(3)
                .jlbhTask(new BootPingPongBenchmark());
        new JLBH(lth).start();
    }


    @Override
    public void init(JLBH jlbh) {
        this.jlbh = jlbh;
        app = SpringApplication.run(SpringBootApplication.class);
    }

    @Override
    public void run(long startTimeNS) {
        HttpResponse<Long> ping = Unirest.post(URL)
                .field(BootPingPongServer.TIME_PARAMETER_NAME, startTimeNS)
                .asObject(Long.class);

        jlbh.sample(System.nanoTime() - ping.getBody());
    }

    @Override
    public void complete() {
        app.close();
    }
}
