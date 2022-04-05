package com.test.back.service;

import com.test.back.dto.ProductDto;

import java.util.List;

public interface ProductService {

    List<ProductDto> getSimilarProducts(String productId);
}
