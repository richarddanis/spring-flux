package com.webflux.proxy.service;

import com.webflux.proxy.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ProductService {

    private final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    private final WebClient webClient;

    public ProductService(WebClient webClient) {
        this.webClient = webClient;
    }
    // @Cacheable("test")
    public Mono<Product> getOrderByOrderId() {
        return webClient
                .get()
                .uri("/products/1")
                .retrieve()
                .onStatus(HttpStatus::is2xxSuccessful, clientResponse ->
                        clientResponse.bodyToMono(String.class).map(e -> new IllegalArgumentException("TestException :)")))
                .bodyToMono(Product.class)
                .doOnSuccess(product -> LOGGER.info("Logging: {}", product))
                .map(product -> {
                    int id = product.getId();
                    product.setId(id + 1);
                    return product;
                });
    }

    public Flux<Product> getProducts() {
        return Flux.range(1,100)
                .delayElements(Duration.ofMillis(1000))
                .flatMap(nextInteger -> webClient
                        .get()
                        .uri(uriBuilder -> uriBuilder.path("/products/{id}").build(nextInteger))
                        .retrieve()
                       .bodyToFlux(Product.class)
                        .doOnEach(product -> LOGGER.info("Logging: {}", product))
                        .map(product -> {
                            int id = product.getId();
                            product.setId(id + 1); // just for fun
                            return product;
                        }));
    }
}
