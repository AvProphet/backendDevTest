package com.reactive.reactivetest.service;

import com.reactive.reactivetest.dto.ProductDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductService {

    Flux<ProductDto> getSimilarProducts(String productId);
}
