package com.invenza.service;

import com.invenza.dto.product.*;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<CategoryResponse> listCategories();
    CategoryResponse createCategory(CategoryCreateRequest request);

    List<ProductResponse> listProducts(String search, UUID categoryId);
    ProductResponse createProduct(ProductCreateRequest request);
    ProductResponse getProduct(UUID id);
    ProductResponse updateProduct(UUID id, ProductUpdateRequest request);
    void deleteProduct(UUID id);
}

