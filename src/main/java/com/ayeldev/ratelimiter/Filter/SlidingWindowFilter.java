package com.ayeldev.ratelimiter.Filter;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SlidingWindowFilter implements Filter {

    private final Gson gson;

    private Map<String, Deque<Long>> requestTimeStamp;
    private int MAX_REQUESTS;
    private long WINDOW_SIZE_MS;
    private ScheduledExecutorService scheduler;

    public SlidingWindowFilter() {
        this.gson = null;
    }

    @Autowired
    public SlidingWindowFilter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        this.requestTimeStamp = new ConcurrentHashMap<>();
        this.MAX_REQUESTS = 5;
        this.WINDOW_SIZE_MS = 60_000L;
        this.scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            long windowStart = System.currentTimeMillis() - WINDOW_SIZE_MS;

            this.requestTimeStamp.forEach((ip, timestamps) -> {
                synchronized (timestamps) {
                    while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
                        timestamps.pollFirst();
                    }
                    if (timestamps.isEmpty()) {
                        this.requestTimeStamp.remove(ip);
                    }
                }
            });

        }, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String ip = httpRequest.getRemoteAddr();
        long now = System.currentTimeMillis();
        long windowStart = now - WINDOW_SIZE_MS;

        this.requestTimeStamp.putIfAbsent(ip, new ArrayDeque<>());
        Deque<Long> timestamps = this.requestTimeStamp.get(ip);

        synchronized (timestamps) {
            while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
                timestamps.pollFirst();
            }

            if (timestamps.size() < MAX_REQUESTS) {
                timestamps.addLast(now);
                chain.doFilter(request, response); // ✅ Payagan
            } else {
                httpResponse.setStatus(429);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write(
                        "{\"error\": \"Too many requests. Please slow down.\"}");
                // ❌ Harangan
            }
        }
    }

    @Override
    public void destroy() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown(); // ✅ dagdag na null check para safe
        }
    }

}
