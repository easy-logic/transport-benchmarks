package com.luxoft.benchmarks.boot.ping;

import net.openhft.chronicle.core.jlbh.JLBH;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class BootPingServer {

    public static final String PING_ENDPOINT = "/ping";
    public static final String TIME_PARAMETER_NAME = "time";
    JLBH jlbh;

    @RequestMapping(value = PING_ENDPOINT, method = RequestMethod.POST)
    @ResponseStatus(code = HttpStatus.OK)
    public void pingHandler(@RequestParam(name = TIME_PARAMETER_NAME) long time) {
        jlbh.sample(System.nanoTime() - time);
    }

    public void setJlbh(JLBH jlbh) {
        this.jlbh = jlbh;
    }
}
