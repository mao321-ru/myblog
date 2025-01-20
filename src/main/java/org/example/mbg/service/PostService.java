package org.example.mbg.service;

import org.example.mbg.dto.PostCreateDto;
import org.example.mbg.dto.PostPreviewDto;

import java.util.List;

public interface PostService {
    List<PostPreviewDto> findPosts(String tags);

    void createPost(PostCreateDto post);
}
