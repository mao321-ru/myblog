package org.example.mbg.repository;

import org.example.mbg.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostRepository {
    Page<Post> findAll(Pageable pageable);

    Page<Post> findByTags(String tags, Pageable pageable);

    void createPost(Post p);

    Optional<Post.Image> findPostImage(long postId);
}
