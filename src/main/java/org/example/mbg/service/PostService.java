package org.example.mbg.service;

import org.example.mbg.dto.PostCreateDto;
import org.example.mbg.dto.PostPreviewDto;
import org.example.mbg.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostService {
    Page<PostPreviewDto> findPosts(String tags, Pageable pageable);

    void createPost(PostCreateDto post);

    Optional<Post.Image> findPostImage(long postId);

    Optional<PostPreviewDto> getPost(long postId);
}
