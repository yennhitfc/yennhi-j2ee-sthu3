package com.example.shopping.service;

import com.example.shopping.model.Category;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CategoryService {
    private final List<Category> categories = new ArrayList<>();
    private final AtomicInteger idSequence = new AtomicInteger();

    public CategoryService() {
        categories.add(createCategory("Điện thoại"));
        categories.add(createCategory("Laptop"));
        categories.add(createCategory("Phụ kiện"));
    }

    public List<Category> findAll() {
        return List.copyOf(categories);
    }

    public Optional<Category> findById(int id) {
        return categories.stream()
                .filter(category -> category.getId() == id)
                .findFirst();
    }

    private Category createCategory(String name) {
        int id = idSequence.incrementAndGet();
        return new Category(id, name);
    }
}
