package org.example.myblog.service;

import org.example.myblog.dto.PostCreateDto;
import org.example.myblog.dto.PostPreviewDto;
import org.example.myblog.dto.PostShowDto;
import org.example.myblog.dto.PostUpdateDto;
import org.example.myblog.model.Post;
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
