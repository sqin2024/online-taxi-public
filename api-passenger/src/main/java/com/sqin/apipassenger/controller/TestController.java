package com.sqin.apipassenger.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {


    @GetMapping("/authTest")
    public String authTest() {
        return "true";
    }

    @GetMapping("/noauthTest")
    public String noauthTest() {
        return "no auth test";
    }
}
