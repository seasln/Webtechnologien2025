package de.htw.berlin.todo_app_backend.controller;

import de.htw.berlin.todo_app_backend.dto.CategoryDTO;
import de.htw.berlin.todo_app_backend.mapper.CategoryMapper;
import de.htw.berlin.todo_app_backend.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService service;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public List<CategoryDTO> getAllCategories() {
        return categoryMapper.toDtoList(service.getAll());
    }

    @PostMapping
    public CategoryDTO createCategory(@RequestBody CategoryDTO categoryDto) {
        return categoryMapper.toDto(service.save(categoryMapper.toEntity(categoryDto)));
    }

    @PutMapping("/{id}")
    public CategoryDTO updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDto) {
        categoryDto.setId(id);
        return categoryMapper.toDto(service.save(categoryMapper.toEntity(categoryDto)));
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        service.delete(id);
    }
}
