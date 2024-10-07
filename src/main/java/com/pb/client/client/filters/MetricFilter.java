package com.pb.client.client.filters;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
public class MetricFilter implements Filter {

    @Autowired
    private MeterRegistry registry;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String uri = httpRequest.getRequestURI();
        if (!uri.contains("prometheus")) {
            Timer timer = Timer.builder("http.request.duration.seconds")
                    .publishPercentiles(0.5, 0.95)
                    .tag("uri", uri)
                    .publishPercentileHistogram()
                    .serviceLevelObjectives(Duration.ofMillis(100))
                    .minimumExpectedValue(Duration.ofMillis(1))
                    .maximumExpectedValue(Duration.ofSeconds(10))
                    .register(registry);

            Timer.Sample sample = Timer.start(registry);
            try {
                filterChain.doFilter(servletRequest, servletResponse);
            } finally {
                sample.stop(timer);
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
