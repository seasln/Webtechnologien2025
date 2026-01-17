package de.htw.berlin.todo_app_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.htw.berlin.todo_app_backend.domain.Category;
import de.htw.berlin.todo_app_backend.domain.ToDoEntry;
import de.htw.berlin.todo_app_backend.repository.CategoryRepository;
import de.htw.berlin.todo_app_backend.repository.ToDoEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository repository;

    @Autowired
    private ToDoEntryRepository todoRepository;

    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();
        repository.deleteAll();
    }

    @Test
    void createCategory_persistsAndReturns() throws Exception {
        Category category = new Category();
        category.setName("Home");
        category.setColorHex("#FFFFFF");

        String responseBody = mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Home"))
                .andExpect(jsonPath("$.colorHex").value("#FFFFFF"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Category saved = objectMapper.readValue(responseBody, Category.class);
        assertThat(repository.findById(saved.getId())).isPresent();
    }

    @Test
    void updateCategory_overwritesEntity() throws Exception {
        Category category = new Category();
        category.setName("Work");
        category.setColorHex("#000000");
        Category saved = repository.save(category);

        Category updated = new Category();
        updated.setName("Personal");
        updated.setColorHex("#ABCDEF");

        mockMvc.perform(put("/categories/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Personal"))
                .andExpect(jsonPath("$.colorHex").value("#ABCDEF"));

        Category refreshed = repository.findById(saved.getId()).orElseThrow();
        assertThat(refreshed.getName()).isEqualTo("Personal");
        assertThat(refreshed.getColorHex()).isEqualTo("#ABCDEF");
    }

    @Test
    void getAllCategories_returnsDescOrder() throws Exception {
        Category first = new Category();
        first.setName("First");
        first.setColorHex("#111111");
        repository.save(first);

        Category second = new Category();
        second.setName("Second");
        second.setColorHex("#222222");
        repository.save(second);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(second.getId()))
                .andExpect(jsonPath("$[1].id").value(first.getId()));
    }

    @Test
    void deleteCategory_removesEntity() throws Exception {
        Category category = new Category();
        category.setName("Delete me");
        category.setColorHex("#123456");
        Category saved = repository.save(category);

        mockMvc.perform(delete("/categories/{id}", saved.getId()))
                .andExpect(status().isOk());

        assertThat(repository.findById(saved.getId())).isEmpty();
    }

    @Test
    void deleteCategory_clearsTodoCategory() throws Exception {
        Category category = new Category();
        category.setName("Work");
        category.setColorHex("#abcdef");
        Category savedCategory = repository.save(category);

        ToDoEntry entry = new ToDoEntry();
        entry.setTitle("Task");
        entry.setCategory(savedCategory);
        ToDoEntry savedEntry = todoRepository.save(entry);

        mockMvc.perform(delete("/categories/{id}", savedCategory.getId()))
                .andExpect(status().isOk());

        ToDoEntry refreshed = todoRepository.findById(savedEntry.getId()).orElseThrow();
        assertThat(refreshed.getCategory()).isNull();
    }
}
