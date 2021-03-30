package com.joseph.cloud;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;


@RestController
public class HomeController {
    @RequestMapping("/")
    public String hello() {
        return "Welcome to Open API " + System.getenv("APP_VERSION") + " !";
    }
}