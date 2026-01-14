package de.htw.berlin.todo_app_backend.mapper;

import de.htw.berlin.todo_app_backend.domain.ToDoEntry;
import de.htw.berlin.todo_app_backend.dto.ToDoEntryDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-14T19:45:30+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Microsoft)"
)
@Component
public class ToDoEntryMapperImpl implements ToDoEntryMapper {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ToDoEntryDTO toDto(ToDoEntry toDoEntry) {
        if ( toDoEntry == null ) {
            return null;
        }

        ToDoEntryDTO toDoEntryDTO = new ToDoEntryDTO();

        toDoEntryDTO.setId( toDoEntry.getId() );
        toDoEntryDTO.setTitle( toDoEntry.getTitle() );
        toDoEntryDTO.setDescription( toDoEntry.getDescription() );
        toDoEntryDTO.setDone( toDoEntry.isDone() );
        toDoEntryDTO.setDueDate( toDoEntry.getDueDate() );
        toDoEntryDTO.setPriority( toDoEntry.getPriority() );
        toDoEntryDTO.setCategory( categoryMapper.toDto( toDoEntry.getCategory() ) );

        return toDoEntryDTO;
    }

    @Override
    public ToDoEntry toEntity(ToDoEntryDTO toDoEntryDto) {
        if ( toDoEntryDto == null ) {
            return null;
        }

        ToDoEntry toDoEntry = new ToDoEntry();

        toDoEntry.setId( toDoEntryDto.getId() );
        toDoEntry.setTitle( toDoEntryDto.getTitle() );
        toDoEntry.setDescription( toDoEntryDto.getDescription() );
        toDoEntry.setDone( toDoEntryDto.isDone() );
        toDoEntry.setDueDate( toDoEntryDto.getDueDate() );
        toDoEntry.setPriority( toDoEntryDto.getPriority() );
        toDoEntry.setCategory( categoryMapper.toEntity( toDoEntryDto.getCategory() ) );

        return toDoEntry;
    }

    @Override
    public List<ToDoEntryDTO> toDtoList(List<ToDoEntry> toDoEntries) {
        if ( toDoEntries == null ) {
            return null;
        }

        List<ToDoEntryDTO> list = new ArrayList<ToDoEntryDTO>( toDoEntries.size() );
        for ( ToDoEntry toDoEntry : toDoEntries ) {
            list.add( toDto( toDoEntry ) );
        }

        return list;
    }
}
