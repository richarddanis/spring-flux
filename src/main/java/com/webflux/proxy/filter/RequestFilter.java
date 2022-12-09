package com.webflux.proxy.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.*;
import java.io.IOException;
import java.util.UUID;

@Component
public class RequestFilter implements Filter, WebMvcConfigurer {

    private final Logger LOGGER = LoggerFactory.getLogger(RequestFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        MDC.put("trace-id", UUID.randomUUID().toString());
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.remove("trace-id");
        }
    }
}
