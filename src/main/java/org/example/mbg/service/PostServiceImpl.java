package org.example.mbg.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.mbg.dto.PostCreateDto;
import org.example.mbg.dto.PostPreviewDto;
import org.example.mbg.mapper.PostMapper;
import org.example.mbg.model.Post;
import org.example.mbg.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Log
@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {
    private final PostRepository repo;
    @Override
    public List<PostPreviewDto> findPosts(String tags) {
        List<Post> posts = ( tags == null || tags.equals( "") ? repo.findAll() : repo.findByTags( tags));
        return posts.stream().map( PostMapper::toPostPreviewDto).toList();
    }

    @Override
    public void createPost(PostCreateDto post) {
        repo.createPost( PostMapper.toPost( post));
    }
}
