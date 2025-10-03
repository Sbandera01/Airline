package com.example.airline.api.services;

import com.example.airline.api.dto.TagDtos;
import java.util.List;

public interface TagService {
    TagDtos.TagResponse create(TagDtos.TagCreateRequest request);
    TagDtos.TagResponse findById(Long id);
    TagDtos.TagResponse findByName(String name);
    List<TagDtos.TagResponse> findAll();
    TagDtos.TagResponse update(Long id, TagDtos.TagCreateRequest request);
    void delete(Long id);
}
