package de.htw.berlin.todo_app_backend.repository;

import de.htw.berlin.todo_app_backend.domain.ToDoEntry;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToDoEntryRepository extends JpaRepository<ToDoEntry, Long> {
    List<ToDoEntry> findAllByOrderByIdDesc();

    @Modifying
    @Query("update ToDoEntry t set t.category = null where t.category.id = :categoryId")
    int clearCategoryFromTodos(@Param("categoryId") Long categoryId);
}
