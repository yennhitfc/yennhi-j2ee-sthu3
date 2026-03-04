package com.example.shopping.service;

import com.example.shopping.model.Category;
import com.example.shopping.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ProductService {
    private final Map<Integer, Product> products = new LinkedHashMap<>();
    private final AtomicInteger idSequence = new AtomicInteger();
    private final CategoryService categoryService;

    public ProductService(CategoryService categoryService) {
        this.categoryService = categoryService;
        initializeSampleData();
    }

    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    public Optional<Product> findById(int id) {
        return Optional.ofNullable(products.get(id));
    }

    public void deleteById(int id) {
        products.remove(id);
    }

    public Product save(Product product) {
        validateCategory(product);

        if (product.getId() <= 0) {
            product.setId(idSequence.incrementAndGet());
        } else {
            idSequence.updateAndGet(current -> Math.max(current, product.getId()));
        }

        Product stored = copyOf(product);
        products.put(stored.getId(), stored);
        return stored;
    }

    private void validateCategory(Product product) {
        Category candidate = product.getCategory();
        if (candidate == null) {
            return;
        }
        categoryService.findById(candidate.getId())
                .ifPresent(product::setCategory);
    }

    private Product copyOf(Product product) {
        return new Product(product.getId(), product.getName(), product.getImage(), product.getPrice(), product.getCategory());
    }

    private void initializeSampleData() {
        save(new Product(0, "iPhone 16", "iphone16.jpg", 32000000L, categoryService.findById(1).orElse(null)));
        save(new Product(0, "Laptop Asus", "laptop.jpg", 65500000L, categoryService.findById(2).orElse(null)));
        save(new Product(0, "Tai nghe Bluetooth", "tainghe.jpg", 750000L, categoryService.findById(3).orElse(null)));
    }
}
