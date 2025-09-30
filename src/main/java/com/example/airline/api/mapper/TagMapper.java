package com.example.airline.api.mapper;

import com.example.airline.api.dto.TagDtos;
import com.example.airline.domain.entities.Tag;
import org.mapstruct.*;

import java.util.Set;

@Mapper(config = MapStructConfig.class, componentModel = "spring")
public interface TagMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "flights", ignore = true)
    Tag toEntity(TagDtos.TagCreateRequest dto);

    TagDtos.TagResponse toResponse(Tag entity);

    Set<TagDtos.TagResponse> toResponseSet(Set<Tag> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Tag entity, TagDtos.TagCreateRequest dto);
}

