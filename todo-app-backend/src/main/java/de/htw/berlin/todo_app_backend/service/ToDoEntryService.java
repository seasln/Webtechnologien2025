package de.htw.berlin.todo_app_backend.service;

import de.htw.berlin.todo_app_backend.domain.ToDoEntry;
import de.htw.berlin.todo_app_backend.repository.ToDoEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class ToDoEntryService {

    @Autowired
    private ToDoEntryRepository repo;

    public List<ToDoEntry> getAll() {
        return repo.findAllByOrderByIdDesc();
    }

    public ToDoEntry getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
    }

    public ToDoEntry save(ToDoEntry entry) {
        if (entry.getId() != null) {
            repo.findById(entry.getId())
                    .map(ToDoEntry::getCreatedAt)
                    .ifPresent(entry::setCreatedAt);
        }
        return repo.save(entry);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
