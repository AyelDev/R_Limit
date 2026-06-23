package com.ayeldev.ratelimiter.Filter;

import com.google.gson.Gson;

import java.io.IOException;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Note: remove @Component if using another component to prevent url mapping to other
// @Component
public class RateLimitingFilter implements Filter {

    private final Gson gson;
    private static final int MAX_REQUESTS_PER_MINUTE = 5;
    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public RateLimitingFilter() {
        this.gson = null;
    }

    @Autowired
    public RateLimitingFilter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        scheduler.scheduleAtFixedRate(() -> {
            requestCounts.clear();
        }, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String clientIp = httpRequest.getRemoteAddr();

        requestCounts.putIfAbsent(clientIp, new AtomicInteger(0));
        int currentCount = requestCounts.get(clientIp).incrementAndGet();

        if (currentCount > MAX_REQUESTS_PER_MINUTE) {
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");

            String jsonResponse = this.gson.toJson(
                    Map.of(
                            "status", 429,
                            "error", "Too Many Requests",
                            "message", "Rate limit exceeded. Please try again later."));
            
           httpResponse.getWriter().write(jsonResponse);
           return;
        }

        chain.doFilter(request, response);

    }

    @Override
    public void destroy() {
        scheduler.shutdown();
    }

}
