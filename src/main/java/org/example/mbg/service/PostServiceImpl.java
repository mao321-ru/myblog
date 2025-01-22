package org.example.mbg.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.mbg.dto.PostCreateDto;
import org.example.mbg.dto.PostPreviewDto;
import org.example.mbg.mapper.PostMapper;
import org.example.mbg.model.Post;
import org.example.mbg.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository repo;
    @Override
    public Page<PostPreviewDto> findPosts(String tags, Pageable pageable) {
        Page<Post> posts = ( tags == null || tags.equals( "")
                ? repo.findAll( pageable)
                : repo.findByTags( tags, pageable));
        return posts.map( PostMapper::toPostPreviewDto);
    }

    @Override
    public void createPost(PostCreateDto post) {
        repo.createPost( PostMapper.toPost( post));
    }

    @Override
    public Optional<Post.Image> findPostImage(long postId) {
        return repo.findPostImage( postId);
    }
}
