package de.htw.berlin.todo_app_backend.repository;

import de.htw.berlin.todo_app_backend.domain.ToDoEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToDoEntryRepository extends CrudRepository<ToDoEntry, Long> {
}
