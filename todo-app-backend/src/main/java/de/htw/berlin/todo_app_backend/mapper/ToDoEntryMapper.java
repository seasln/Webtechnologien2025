package de.htw.berlin.todo_app_backend.mapper;

import de.htw.berlin.todo_app_backend.config.MapStructConfig;
import de.htw.berlin.todo_app_backend.domain.ToDoEntry;
import de.htw.berlin.todo_app_backend.dto.ToDoEntryDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class, uses = CategoryMapper.class)
public interface ToDoEntryMapper {
	ToDoEntryDTO toDto(ToDoEntry toDoEntry);

	ToDoEntry toEntity(ToDoEntryDTO toDoEntryDto);

	List<ToDoEntryDTO> toDtoList(List<ToDoEntry> toDoEntries);
}
