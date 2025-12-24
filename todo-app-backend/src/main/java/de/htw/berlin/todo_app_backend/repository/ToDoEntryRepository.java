package de.htw.berlin.todo_app_backend.repository;

import de.htw.berlin.todo_app_backend.domain.ToDoEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToDoEntryRepository extends JpaRepository<ToDoEntry, Long> {
    public List<ToDoEntry> findAllByOrderByIdDesc();
}
