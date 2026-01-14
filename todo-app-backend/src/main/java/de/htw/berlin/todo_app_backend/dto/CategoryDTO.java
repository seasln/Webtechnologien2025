package de.htw.berlin.todo_app_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryDTO {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String colorHex;
}
