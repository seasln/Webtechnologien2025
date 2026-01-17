package de.htw.berlin.todo_app_backend.service;

import de.htw.berlin.todo_app_backend.domain.Category;
import de.htw.berlin.todo_app_backend.repository.CategoryRepository;
import de.htw.berlin.todo_app_backend.repository.ToDoEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repo;
    @Autowired
    private ToDoEntryRepository todoEntryRepository;

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

    @Transactional
    public void delete(Long id) {
        todoEntryRepository.clearCategoryFromTodos(id);
        repo.deleteById(id);
    }
}
