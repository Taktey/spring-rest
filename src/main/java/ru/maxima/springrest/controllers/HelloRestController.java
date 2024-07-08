package ru.maxima.springrest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HelloRestController {

    @GetMapping("/sayHello")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping("/sayGoodBye")
    public String goodbye() {
        return "Good bye!";
    }

}
