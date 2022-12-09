package com.webflux.proxy.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfiguration {

    private final String URI = "https://dummyjson.com/";

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .baseUrl(URI)
                .filter((request, next) ->
                        next.exchange(request)
                                .doOnNext(value ->
                                        Optional.ofNullable(MDC.getCopyOfContextMap())
                                                .ifPresent(MDC::setContextMap)))
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
