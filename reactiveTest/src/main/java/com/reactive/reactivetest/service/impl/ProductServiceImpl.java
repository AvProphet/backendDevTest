package com.reactive.reactivetest.service.impl;

import com.reactive.reactivetest.config.HttpConfig;
import com.reactive.reactivetest.dto.ProductDto;
import com.reactive.reactivetest.exception.NotFoundException;
import com.reactive.reactivetest.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final WebClient webClient;

    private final String URL_SIMILAR_IDS;
    private final String URL_PRODUCT;

    @Autowired
    public ProductServiceImpl(HttpConfig httpConfig, WebClient webClient) {
        this.webClient = webClient;

        URL_SIMILAR_IDS = httpConfig.getUrl(HttpConfig.Endpoint.SIMILAR_IDS);
        URL_PRODUCT = httpConfig.getUrl(HttpConfig.Endpoint.PRODUCT_URL);
    }

    @Override
    public Flux<ProductDto> getSimilarProducts(String productId) {
        Flux<Integer> similarIds = getSimilarIds(productId);

        Mono<List<ProductDto>> mono = similarIds.flatMap(
                        id -> getProducts(id.toString())
                ).collect(Collectors.toList())
                .onErrorResume(throwable -> {
                    log.error("Error trying to get products from ({}) or doesnt exist", productId, throwable);
                    return Mono.error((NotFoundException::new));
                });

        return mono
                .flatMapMany(Flux::fromIterable)
                .log();
    }

    private Flux<Integer> getSimilarIds(String productId) {
        return webClient.get()
                .uri(URL_SIMILAR_IDS, productId)
                .retrieve()
                .bodyToFlux(Integer.class)
                .onErrorResume(error -> {
                    log.error("Error while searching for {}", productId, error);
                    return Mono.error(NotFoundException::new);
                });
    }

    private Flux<ProductDto> getProducts(String productId) {
        return webClient.get()
                .uri(URL_PRODUCT, productId)
                .retrieve()
                .bodyToFlux(ProductDto.class)
                .onErrorResume(error -> {
                    log.error("Error while retrieving product {}:", productId, error);
                    return Flux.empty();
                });
    }
}
