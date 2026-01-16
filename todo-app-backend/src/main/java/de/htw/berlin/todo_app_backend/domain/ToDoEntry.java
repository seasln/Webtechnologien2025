package de.htw.berlin.todo_app_backend.domain;

import de.htw.berlin.todo_app_backend.enums.Priority;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class ToDoEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Size(max = 25)
    private String title;
    @Size(max = 200)
    private String description;
    private boolean done;
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    private Priority priority;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
