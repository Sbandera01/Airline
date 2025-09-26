package com.example.airline.domain.repositories;

import com.example.airline.BaseIntegrationTest;
import com.example.airline.domain.entities.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TagRepositoryTest extends BaseIntegrationTest {

    @Autowired
    TagRepository tagRepository;

    @Test
    void shouldFindTagByName() {
        tagRepository.save(Tag.builder().name("promo").build());

        Optional<Tag> found = tagRepository.findByName("promo");

        assertThat(found).isPresent();
    }

    @Test
    void shouldFindTagsByNameList() {
        tagRepository.saveAll(List.of(
                Tag.builder().name("eco").build(),
                Tag.builder().name("red-eye").build()
        ));

        List<Tag> tags = tagRepository.findByNameIn(List.of("eco", "red-eye"));

        assertThat(tags).hasSize(2);
    }
}


