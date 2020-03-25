package com.luxoft.benchmarks.boot.ping_pong;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BootPingPongServer {

    public static final String PING_PONG_ENDPOINT = "/ping-pong";
    public static final String TIME_PARAMETER_NAME = "time";

    @RequestMapping(value = PING_PONG_ENDPOINT, method = RequestMethod.POST)
    @ResponseBody
    public long pongHandler(@RequestParam(name = TIME_PARAMETER_NAME) long time) {
        return time;
    }

}
