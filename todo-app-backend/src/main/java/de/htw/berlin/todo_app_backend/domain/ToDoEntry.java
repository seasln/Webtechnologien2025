package de.htw.berlin.todo_app_backend.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ToDoEntry {
    private int id;
    private String description;
    private boolean done;
}
