package com.ayeldev.ratelimiter.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.gson.Gson;

@Configuration
public class JsonConfig {

    @Bean
    public Gson gson() {
        return new Gson();
    }
}
