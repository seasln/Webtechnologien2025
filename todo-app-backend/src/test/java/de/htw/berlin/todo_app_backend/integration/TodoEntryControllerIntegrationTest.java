package de.htw.berlin.todo_app_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.htw.berlin.todo_app_backend.domain.ToDoEntry;
import de.htw.berlin.todo_app_backend.enums.Priority;
import de.htw.berlin.todo_app_backend.repository.ToDoEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

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
class TodoEntryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ToDoEntryRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void createTodo_persistsAndReturns() throws Exception {
        ToDoEntry entry = new ToDoEntry();
        entry.setTitle("Buy milk");
        entry.setDescription("2L");
        entry.setDone(false);
        entry.setDueDate(LocalDate.of(2025, 1, 15));
        entry.setPriority(Priority.HIGH);

        String responseBody = mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Buy milk"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ToDoEntry saved = objectMapper.readValue(responseBody, ToDoEntry.class);
        assertThat(repository.findById(saved.getId())).isPresent();
    }

    @Test
    void getAllTodos_returnsDescOrder() throws Exception {
        ToDoEntry first = new ToDoEntry();
        first.setTitle("First");
        repository.save(first);

        ToDoEntry second = new ToDoEntry();
        second.setTitle("Second");
        repository.save(second);

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(second.getId()))
                .andExpect(jsonPath("$[1].id").value(first.getId()));
    }

    @Test
    void updateTodo_overwritesEntity() throws Exception {
        ToDoEntry entry = new ToDoEntry();
        entry.setTitle("Old");
        entry.setDone(false);
        ToDoEntry saved = repository.save(entry);

        ToDoEntry updated = new ToDoEntry();
        updated.setTitle("New");
        updated.setDescription("Updated");
        updated.setDone(true);
        updated.setDueDate(LocalDate.of(2025, 2, 1));
        updated.setPriority(Priority.MEDIUM);

        mockMvc.perform(put("/todos/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.title").value("New"))
                .andExpect(jsonPath("$.description").value("Updated"))
                .andExpect(jsonPath("$.done").value(true))
                .andExpect(jsonPath("$.priority").value("MEDIUM"));

        ToDoEntry refreshed = repository.findById(saved.getId()).orElseThrow();
        assertThat(refreshed.getTitle()).isEqualTo("New");
        assertThat(refreshed.isDone()).isTrue();
        assertThat(refreshed.getPriority()).isEqualTo(Priority.MEDIUM);
    }

    @Test
    void deleteTodo_removesEntity() throws Exception {
        ToDoEntry entry = new ToDoEntry();
        entry.setTitle("Delete me");
        ToDoEntry saved = repository.save(entry);

        mockMvc.perform(delete("/todos/{id}", saved.getId()))
                .andExpect(status().isOk());

        assertThat(repository.findById(saved.getId())).isEmpty();
    }
}
