package de.htw.berlin.todo_app_backend.dto;

import de.htw.berlin.todo_app_backend.enums.Priority;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ToDoEntryDTO {
    private Long id;
    @NotNull
    private String title;
    private String description;
    private boolean done;
    private LocalDate dueDate;
    private Priority priority;
    private CategoryDTO category;
}
