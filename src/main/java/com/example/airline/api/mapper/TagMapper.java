package com.example.airline.api.mapper;

import com.example.airline.api.dto.TagDtos;
import com.example.airline.domain.entities.Tag;

public class TagMapper {

    public static Tag toEntity(TagDtos.TagCreateRequest dto) {
        if (dto == null) return null;
        return Tag.builder().name(dto.name()).build();
    }

    public static TagDtos.TagResponse toResponse(Tag entity) {
        if (entity == null) return null;
        return new TagDtos.TagResponse(entity.getId(), entity.getName());
    }
}

