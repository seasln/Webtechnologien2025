package de.htw.berlin.todo_app_backend.service;

import de.htw.berlin.todo_app_backend.domain.Category;
import de.htw.berlin.todo_app_backend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repo;

    public List<Category> getAll() {
        return repo.findAllByOrderByIdDesc();
    }

    public Category getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public Category save(Category category) {
        return repo.save(category);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
