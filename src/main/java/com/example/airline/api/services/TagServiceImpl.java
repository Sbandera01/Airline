package com.example.airline.api.services;

import com.example.airline.api.dto.TagDtos;
import com.example.airline.api.mapper.TagMapper;
import com.example.airline.domain.entities.Tag;
import com.example.airline.domain.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    @Transactional
    public TagDtos.TagResponse create(TagDtos.TagCreateRequest request) {
        tagRepository.findByName(request.name()).ifPresent(t -> {
            throw new IllegalArgumentException("Tag with name " + request.name() + " already exists");
        });

        Tag tag = tagMapper.toEntity(request);
        tag = tagRepository.save(tag);
        return tagMapper.toResponse(tag);
    }

    @Override
    public TagDtos.TagResponse findById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found with id: " + id));
        return tagMapper.toResponse(tag);
    }

    @Override
    public TagDtos.TagResponse findByName(String name) {
        Tag tag = tagRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found with name: " + name));
        return tagMapper.toResponse(tag);
    }

    @Override
    public List<TagDtos.TagResponse> findAll() {
        return tagRepository.findAll().stream()
                .map(tagMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TagDtos.TagResponse update(Long id, TagDtos.TagCreateRequest request) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found with id: " + id));

        tag.setName(request.name());
        tag = tagRepository.save(tag);
        return tagMapper.toResponse(tag);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new IllegalArgumentException("Tag not found with id: " + id);
        }
        tagRepository.deleteById(id);
    }
}
