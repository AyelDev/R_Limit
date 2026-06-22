package com.ayeldev.ratelimiter.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
public class TestController {

    private final Gson gson;
    private final String jsonContent = "application/json";
    private final String charEncoding = "UTF-8";

    @Autowired
    public TestController(Gson gson) {
        this.gson = gson;
    }

    public void setResponse(HttpServletResponse response, int status) {
        response.setStatus(status);
        response.setContentType(jsonContent);
        response.setCharacterEncoding(charEncoding);
    }

    @GetMapping("/test")
    public String testRateLimit(HttpServletResponse response) {
        this.setResponse(response, 200);
        return this.gson.toJson(Map.of("message", "Request reached successfully"));
    }

    @GetMapping("/sliding-windows")
    // public String slidingWindows(HttpServletResponse response) {
    //     // this.setResponse(response);
    // }

}