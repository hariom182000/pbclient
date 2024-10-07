package com.pb.client.client.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
public class HelloController {

    private final RestTemplate restTemplate;

    @Autowired
    public HelloController(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/hello")
    public byte[] sayHello() {
        log.info("hello from client hello controller");
        return restTemplate.getForObject("http://pbserver:9082/hello", byte[].class);
    }
}
