package de.htw.berlin.todo_app_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/todos")
public class TodoController {
    @GetMapping
    public String demoApi() {
        return "Hello World.";
    }
}
