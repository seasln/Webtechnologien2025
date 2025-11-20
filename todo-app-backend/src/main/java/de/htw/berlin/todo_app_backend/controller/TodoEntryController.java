package de.htw.berlin.todo_app_backend.controller;

import de.htw.berlin.todo_app_backend.domain.ToDoEntry;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/todos")
@CrossOrigin(origins = "https://webtechnologien2025-frontend-7qhu.onrender.com/")
public class TodoEntryController {
    @GetMapping
    public List<ToDoEntry> getAllTodos() {
        ToDoEntry toDoEntry1 = new ToDoEntry(1, "Spring Boot Backend erstellen", true, "heute", "Work Project", "hoch");
        ToDoEntry toDoEntry2 = new ToDoEntry(2, "Vue.js Frontend beginnen", false, "übermorgen", "Work Project" , "hoch");
        ToDoEntry toDoEntry3 = new ToDoEntry(3, "Milestone 1 auf Github pushen", false, "morgen" , "Uni" , "niedrig");
        ToDoEntry toDoEntry4 = new ToDoEntry(3, "Zimmer aufräumen", false, "morgen" , "Privat" , "niedrig");


        return List.of(toDoEntry1, toDoEntry2, toDoEntry3, toDoEntry4);
    }

    @GetMapping("/{id}") //{id} ist Platzhalter zb für todo1, todo2
    public ToDoEntry getToDoById(@PathVariable int id){ //@PathVariavle nimmt id aus URL und speichert sie in der variable "id" zb /todos/5 --> id = 5
        // hier später aus der DB suchen
        //für jetzt: Bsp todo zurückgeben

        return new ToDoEntry(id, "Example Todo" , false, "heute" , "Arbeit" , "hoch");
    }
}
