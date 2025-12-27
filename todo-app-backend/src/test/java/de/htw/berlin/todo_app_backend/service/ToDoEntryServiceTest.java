package de.htw.berlin.todo_app_backend.service;

import de.htw.berlin.todo_app_backend.domain.ToDoEntry;
import de.htw.berlin.todo_app_backend.repository.ToDoEntryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ToDoEntryServiceTest {

    @Mock
    private ToDoEntryRepository repo;

    @InjectMocks
    private ToDoEntryService service;

    @Test
    void getAll_returnsEntriesFromRepository() {
        ToDoEntry entry = new ToDoEntry();
        entry.setTitle("First");
        List<ToDoEntry> entries = List.of(entry);
        when(repo.findAllByOrderByIdDesc()).thenReturn(entries);

        Iterable<ToDoEntry> result = service.getAll();

        assertThat(result).containsExactly(entry);
        verify(repo).findAllByOrderByIdDesc();
    }

    @Test
    void getById_returnsEntryWhenPresent() {
        ToDoEntry entry = new ToDoEntry();
        entry.setId(42L);
        when(repo.findById(42L)).thenReturn(Optional.of(entry));

        ToDoEntry result = service.getById(42L);

        assertThat(result).isSameAs(entry);
        verify(repo).findById(42L);
    }

    @Test
    void getById_throwsWhenMissing() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Todo not found");
    }

    @Test
    void save_delegatesToRepository() {
        ToDoEntry entry = new ToDoEntry();
        entry.setTitle("Save me");
        when(repo.save(entry)).thenReturn(entry);

        ToDoEntry result = service.save(entry);

        assertThat(result).isSameAs(entry);
        verify(repo).save(entry);
    }

    @Test
    void delete_delegatesToRepository() {
        service.delete(7L);

        verify(repo).deleteById(7L);
    }
}
