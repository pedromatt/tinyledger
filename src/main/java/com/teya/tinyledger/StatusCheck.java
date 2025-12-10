package com.teya.tinyledger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusCheck {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
