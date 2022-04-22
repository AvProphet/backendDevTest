package com.reactive.reactivetest.service;

import com.reactive.reactivetest.dto.ProductDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductService {

    Mono<List<ProductDto>> getSimilarProducts(String productId);
}
