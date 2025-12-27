package de.htw.berlin.todo_app_backend.service;

import de.htw.berlin.todo_app_backend.domain.Category;
import de.htw.berlin.todo_app_backend.repository.CategoryRepository;
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
class CategoryServiceTest {

    @Mock
    private CategoryRepository repo;

    @InjectMocks
    private CategoryService service;

    @Test
    void getAll_returnsEntriesFromRepository() {
        Category category = new Category();
        category.setName("Home");
        category.setColorHex("#FFFFFF");
        List<Category> categories = List.of(category);
        when(repo.findAllByOrderByIdDesc()).thenReturn(categories);

        Iterable<Category> result = service.getAll();

        assertThat(result).containsExactly(category);
        verify(repo).findAllByOrderByIdDesc();
    }

    @Test
    void getById_returnsCategoryWhenPresent() {
        Category category = new Category();
        category.setId(12L);
        when(repo.findById(12L)).thenReturn(Optional.of(category));

        Category result = service.getById(12L);

        assertThat(result).isSameAs(category);
        verify(repo).findById(12L);
    }

    @Test
    void getById_throwsWhenMissing() {
        when(repo.findById(13L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(13L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found");
    }

    @Test
    void save_delegatesToRepository() {
        Category category = new Category();
        category.setName("Work");
        category.setColorHex("#000000");
        when(repo.save(category)).thenReturn(category);

        Category result = service.save(category);

        assertThat(result).isSameAs(category);
        verify(repo).save(category);
    }

    @Test
    void delete_delegatesToRepository() {
        service.delete(5L);

        verify(repo).deleteById(5L);
    }
}
