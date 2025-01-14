package org.example.mbg.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.mbg.dto.PostPreviewDto;
import org.example.mbg.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Log
@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {
    private final PostRepository repo;
    @Override
    public List<PostPreviewDto> findPosts() {
        List<PostPreviewDto> posts = repo.findAll().stream().map( PostPreviewDto::fromModel).toList();
        return posts;
    }
}
