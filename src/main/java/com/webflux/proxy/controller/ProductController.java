package com.webflux.proxy.controller;

import com.webflux.proxy.model.Product;
import com.webflux.proxy.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.function.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private final ProductService orderService;

    public ProductController(ProductService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Flux<ServerResponse> getAllOrder() {
        return Flux.empty();
    }

    @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Product>> getOrderByOrder(@PathParam(value = "productId") String productId) {
        return orderService.getOrderByOrderId()
                .subscribeOn(Schedulers.boundedElastic())
                .map(successResponse -> new ResponseEntity<>(successResponse, HttpStatus.OK))
                .onErrorResume(IllegalArgumentException.class, error -> Mono.error(new ResponseStatusException(HttpStatus.ALREADY_REPORTED, "Valami szöveg")))
                .onErrorResume(Exception.class, error -> Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)));
    }

    @GetMapping(value = "/all", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Product>> getProducts() {
        return orderService.getProducts()
                .subscribeOn(Schedulers.boundedElastic())
                .map(successResponse -> ServerSentEvent.builder(successResponse).comment("comment").event("subscribed").build());
    }

    // do not use
    @GetMapping("/blocking")
    public ServerResponse getOrderByO(@PathParam(value = "productId") String productId) {
        LOGGER.info("New request: {}", productId);

        try {
            orderService.getOrderByOrderId().block();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED, "teszt");
        }

        return null;
    }

}
