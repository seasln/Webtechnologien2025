package de.htw.berlin.todo_app_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryDTO {
    private Long id;
    @NotNull
    @Size(max = 25, message = "Maximum of 25 characters allowed")
    private String name;
    @NotNull
    private String colorHex;
}
