package de.htw.berlin.todo_app_backend.dto;

import de.htw.berlin.todo_app_backend.enums.Priority;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ToDoEntryDTO {
    private Long id;
    @NotNull
    @Size(max = 25, message = "Maximum of 25 characters allowed")
    private String title;
    @Size(max = 200, message = "Maximum of 200 characters allowed")
    private String description;
    private boolean done;
    private LocalDate dueDate;
    private Priority priority;
    private CategoryDTO category;
}
