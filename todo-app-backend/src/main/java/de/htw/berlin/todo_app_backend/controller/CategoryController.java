package de.htw.berlin.todo_app_backend.controller;

import de.htw.berlin.todo_app_backend.domain.Category;
import de.htw.berlin.todo_app_backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService service;

    @GetMapping
    public Iterable<Category> getAllCategories() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return service.save(category);
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        return service.save(category);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        service.delete(id);
    }
}
