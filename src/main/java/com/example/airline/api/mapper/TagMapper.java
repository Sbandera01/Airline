package com.example.airline.api.mapper;

import com.example.airline.api.dto.TagDtos;
import com.example.airline.domain.entities.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "flights",ignore = true)
    Tag toEntity(TagDtos.TagCreateRequest dto);

    TagDtos.TagResponse toResponse(Tag entity);
}

