package org.example.mbg.service;

import org.example.mbg.dto.PostCreateDto;
import org.example.mbg.dto.PostPreviewDto;
import org.example.mbg.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {
    List<PostPreviewDto> findPosts(String tags);

    void createPost(PostCreateDto post);

    Optional<Post.Image> findPostImage(long postId);
}
