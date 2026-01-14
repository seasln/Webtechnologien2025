package de.htw.berlin.todo_app_backend.controller;

import de.htw.berlin.todo_app_backend.dto.ToDoEntryDTO;
import de.htw.berlin.todo_app_backend.mapper.ToDoEntryMapper;
import de.htw.berlin.todo_app_backend.service.ToDoEntryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
@AllArgsConstructor
public class TodoEntryController {

    private final ToDoEntryService service;
    private final ToDoEntryMapper toDoEntryMapper;

    @GetMapping
    public List<ToDoEntryDTO> getAllTodos() {
        return toDoEntryMapper.toDtoList(service.getAll());
    }

    @GetMapping("/{id}")
    public ToDoEntryDTO getTodoById(@PathVariable Long id) {
        return toDoEntryMapper.toDto(service.getById(id));
    }

    @PostMapping
    public ToDoEntryDTO createTodo(@RequestBody ToDoEntryDTO entryDto) {
        return toDoEntryMapper.toDto(service.save(toDoEntryMapper.toEntity(entryDto)));
    }

    @PutMapping("/{id}")
    public ToDoEntryDTO updateTodo(@PathVariable Long id, @RequestBody ToDoEntryDTO entryDto) {
        entryDto.setId(id);
        return toDoEntryMapper.toDto(service.save(toDoEntryMapper.toEntity(entryDto)));
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        service.delete(id);
    }
}
