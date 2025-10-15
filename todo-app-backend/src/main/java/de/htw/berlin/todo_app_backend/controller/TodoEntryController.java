package de.htw.berlin.todo_app_backend.controller;

import de.htw.berlin.todo_app_backend.domain.ToDoEntry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/todos")
public class TodoEntryController {
    @GetMapping
    public List<ToDoEntry> demoApi() {
        ToDoEntry toDoEntry1 = new ToDoEntry(1, "Spring Boot Backend erstellen", true);
        ToDoEntry toDoEntry2 = new ToDoEntry(2, "Vue.js Frontend beginnen", false);
        ToDoEntry toDoEntry3 = new ToDoEntry(3, "Milestone 1 auf Github pushen", false);
        return List.of(toDoEntry1, toDoEntry2, toDoEntry3);
    }
}
