package de.htw.berlin.todo_app_backend.controller;

import de.htw.berlin.todo_app_backend.domain.ToDoEntry;
import de.htw.berlin.todo_app_backend.service.ToDoEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/todos")
@CrossOrigin(origins = {
        "https://webtechnologien2025-frontend-7qhu.onrender.com",
        "https://webtechnologien2025-te9p.onrender.com/",
        "http://localhost:5173",
        "http://localhost:3000"
})
public class TodoEntryController {

    @Autowired
    private ToDoEntryService service;    // Neuer zentraler Service

    @GetMapping
    public Iterable<ToDoEntry> getAllTodos() {
        return service.getAll();         // Holt alles aus der DB
    }

    @GetMapping("/{id}")
    public ToDoEntry getTodoById(@PathVariable Long id) {
        return service.getById(id);      // Holt ein Todo aus der DB
    }

    @PostMapping //post route
    public ToDoEntry createTodo(@RequestBody ToDoEntry entry) {
        return service.save(entry);      // Speichert in der DB
    }

    @PutMapping("/{id}")
    public ToDoEntry updateTodo(@PathVariable Long id, @RequestBody ToDoEntry entry) {
        entry.setId(id);                 // ID aus URL übernehmen
        return service.save(entry);      // Überschreibt / updated
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        service.delete(id);              // <-- Löscht aus der DB
    }
}
