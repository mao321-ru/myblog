package org.example.mbg.service;

import org.example.mbg.dto.PostCreateDto;
import org.example.mbg.dto.PostPreviewDto;
import org.example.mbg.dto.PostShowDto;
import org.example.mbg.dto.PostUpdateDto;
import org.example.mbg.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostService {
    Page<PostPreviewDto> findPosts(String tags, Pageable pageable);

    Optional<Post.Image> findPostImage(long postId);

    Optional<PostShowDto> getPost(long postId);

    void createPost(PostCreateDto post);

    void updatePost(PostUpdateDto post);

    void deletePost(long postId);

}
