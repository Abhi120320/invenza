package com.invenza.service.impl;

import com.invenza.dto.product.*;
import com.invenza.entity.Category;
import com.invenza.entity.Location;
import com.invenza.entity.Product;
import com.invenza.entity.Stock;
import com.invenza.exception.ConflictException;
import com.invenza.exception.ResourceNotFoundException;
import com.invenza.repository.CategoryRepository;
import com.invenza.repository.LocationRepository;
import com.invenza.repository.ProductRepository;
import com.invenza.repository.StockRepository;
import com.invenza.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final StockRepository stockRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              LocationRepository locationRepository,
                              StockRepository stockRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> listCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        categoryRepository.findByNameIgnoreCase(request.getName()).ifPresent(c -> {
            throw new ConflictException("Category already exists");
        });
        Category category = new Category(request.getName());
        Category saved = categoryRepository.save(category);
        return CategoryResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> listProducts(String search, UUID categoryId) {
        String query = (search != null && !search.isBlank()) ? search.trim() : null;
        List<Product> products = productRepository.searchProducts(query, categoryId);
        return products.stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        String sku = request.getSku();
        if (productRepository.existsBySku(sku)) {
            throw new ConflictException("SKU already exists");
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }

        Product product = new Product(
                request.getName(),
                sku,
                category,
                request.getUom(),
                request.getReorderThreshold(),
                request.getImageData()
        );

        Product savedProduct = productRepository.save(product);

        // Seed initial stock if provided
        if (request.getInitialStock() != null && request.getInitialLocationId() != null
                && request.getInitialStock().compareTo(BigDecimal.ZERO) > 0) {
            Location location = locationRepository.findById(request.getInitialLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

            Stock stock = new Stock(
                    savedProduct,
                    location,
                    request.getInitialStock(),
                    request.getPerUnitCost()
            );
            stockRepository.save(stock);
        }

        return getProduct(savedProduct.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(UUID id) {
        Product product = productRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return ProductResponse.fromEntity(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(UUID id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (request.getName() != null) product.setName(request.getName());
        if (request.getSku() != null) {
            String newSku = request.getSku().trim().toUpperCase();
            if (!newSku.equalsIgnoreCase(product.getSku()) && productRepository.existsBySku(newSku)) {
                throw new ConflictException("SKU already exists");
            }
            product.setSku(newSku);
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            product.setCategory(category);
        }
        if (request.getUom() != null) product.setUom(request.getUom());
        if (request.getReorderThreshold() != null) product.setReorderThreshold(request.getReorderThreshold());
        if (request.getImageData() != null) product.setImageData(request.getImageData());

        Product updated = productRepository.save(product);
        return getProduct(updated.getId());
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepository.delete(product);
    }
}

