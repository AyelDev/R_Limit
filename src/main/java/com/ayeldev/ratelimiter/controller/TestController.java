package com.ayeldev.ratelimiter.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api")
public class TestController {

    private final Gson gson;

    @Autowired
    public TestController(Gson gson){
        this.gson = gson;
    }

    @GetMapping("/test")
    public String testRateLimit(HttpServletResponse response){
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
        return this.gson.toJson(Map.of("message", "Request reached successfully"));
    }

    @GetMapping("/sliding-windows")
    public String slidingWindows(HttpServletResponse response) {
        return new String();
    }
    
}