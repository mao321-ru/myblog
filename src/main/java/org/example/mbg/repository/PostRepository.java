package org.example.mbg.repository;

import org.example.mbg.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    List<Post> findAll();

    List<Post> findByTags(String tags);

    void createPost(Post p);

    Optional<Post.Image> findPostImage(long postId);
}
