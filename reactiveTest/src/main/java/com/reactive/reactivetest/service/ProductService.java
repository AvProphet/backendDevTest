package com.reactive.reactivetest.service;

import com.reactive.reactivetest.dto.ProductDto;
import reactor.core.publisher.Flux;

public interface ProductService {

    Flux<ProductDto> getSimilarProducts(String productId);
}
