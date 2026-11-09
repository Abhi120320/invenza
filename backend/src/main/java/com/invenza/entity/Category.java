package com.invenza.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain entity representing a product category.
 */
@Entity
@Table(name = "categories", uniqueConstraints = {
    @UniqueConstraint(name = "uq_category_name", columnNames = {"name"})
})
public class Category extends BaseEntity {

    @Column(name = "name", length = 255, nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    public Category() {
    }

    public Category(UUID id) {
        super(id);
    }

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}

