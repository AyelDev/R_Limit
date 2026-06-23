package com.ayeldev.ratelimiter.Config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ayeldev.ratelimiter.Filter.RateLimitingFilter;
import com.ayeldev.ratelimiter.Filter.SlidingWindowFilter;

@Configuration
public class RateLimitingFilterConfig {

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilter() {
        FilterRegistrationBean<RateLimitingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RateLimitingFilter());
        registration.addUrlPatterns("/api/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean<SlidingWindowFilter> SlidingWindowFilter() {
        FilterRegistrationBean<SlidingWindowFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SlidingWindowFilter());
        registration.addUrlPatterns("/v2/api/*");
        return registration;
    }

}
