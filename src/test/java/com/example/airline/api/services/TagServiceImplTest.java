package com.example.airline.api.services;

import com.example.airline.domain.repositories.TagRepository;
import com.example.airline.api.dto.TagDtos;
import com.example.airline.domain.entities.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TagService Tests")
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    @Test
    @DisplayName("Crear tag exitosamente")
    void testCreateTag_Success() {
        // Arrange
        TagDtos.TagCreateRequest request = new TagDtos.TagCreateRequest("Direct");
        Tag tag = Tag.builder().id(1L).name("Direct").build();

        when(tagRepository.findByName("Direct")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        // Act
        TagDtos.TagResponse response = tagService.create(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Direct");
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    @DisplayName("Crear tag - Nombre ya existe")
    void testCreateTag_NameAlreadyExists() {
        // Arrange
        TagDtos.TagCreateRequest request = new TagDtos.TagCreateRequest("Direct");
        Tag existing = Tag.builder().id(1L).name("Direct").build();

        when(tagRepository.findByName("Direct")).thenReturn(Optional.of(existing));

        // Act & Assert
        assertThatThrownBy(() -> tagService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Buscar tag por ID")
    void testFindById_Success() {
        // Arrange
        Tag tag = Tag.builder().id(1L).name("Direct").build();
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        // Act
        TagDtos.TagResponse response = tagService.findById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Direct");
    }

    @Test
    @DisplayName("Buscar tag por nombre")
    void testFindByName_Success() {
        // Arrange
        Tag tag = Tag.builder().id(1L).name("Direct").build();
        when(tagRepository.findByName("Direct")).thenReturn(Optional.of(tag));

        // Act
        TagDtos.TagResponse response = tagService.findByName("Direct");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Direct");
    }

    @Test
    @DisplayName("Buscar todos los tags")
    void testFindAll() {
        // Arrange
        List<Tag> tags = List.of(
                Tag.builder().id(1L).name("Direct").build(),
                Tag.builder().id(2L).name("Popular").build()
        );
        when(tagRepository.findAll()).thenReturn(tags);

        // Act
        List<TagDtos.TagResponse> responses = tagService.findAll();

        // Assert
        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("Actualizar tag")
    void testUpdateTag_Success() {
        // Arrange
        Tag tag = Tag.builder().id(1L).name("Direct").build();
        TagDtos.TagCreateRequest request = new TagDtos.TagCreateRequest("Non-Stop");

        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        // Act
        TagDtos.TagResponse response = tagService.update(1L, request);

        // Assert
        assertThat(response).isNotNull();
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    @DisplayName("Eliminar tag")
    void testDeleteTag_Success() {
        // Arrange
        when(tagRepository.existsById(1L)).thenReturn(true);

        // Act
        tagService.delete(1L);

        // Assert
        verify(tagRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar tag - No encontrado")
    void testDeleteTag_NotFound() {
        // Arrange
        when(tagRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> tagService.delete(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }
}
