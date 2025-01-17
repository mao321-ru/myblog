package org.example.mbg.repository;

import org.example.mbg.model.Post;

import java.util.List;

public interface PostRepository {
    List<Post> findAll();

    void createPost(Post p);
}
