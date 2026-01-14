package de.htw.berlin.todo_app_backend.mapper;

import de.htw.berlin.todo_app_backend.config.MapStructConfig;
import de.htw.berlin.todo_app_backend.domain.Category;
import de.htw.berlin.todo_app_backend.dto.CategoryDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface CategoryMapper {
	CategoryDTO toDto(Category category);

	Category toEntity(CategoryDTO categoryDto);

    List<CategoryDTO> toDtoList(List<Category> categories);
}
