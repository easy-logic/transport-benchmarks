package io.easylogic.benchmarks.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static io.easylogic.benchmarks.Common.SPRING_BOOT_PING_PONG_ENDPOINT;
import static io.easylogic.benchmarks.Common.SPRING_BOOT_TIME_PARAMETER_NAME;

@Controller
public class PongController {

    @RequestMapping(value = SPRING_BOOT_PING_PONG_ENDPOINT, method = RequestMethod.POST)
    @ResponseBody
    public long pongHandler(@RequestParam(name = SPRING_BOOT_TIME_PARAMETER_NAME) long time) {
        return time;
    }

}