package org.example.myblog.repository;

import org.example.myblog.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostRepository {
    Page<Post> findAll(Pageable pageable);

    Page<Post> findByTags(String tags, Pageable pageable);

    Optional<Post.Image> findPostImage(long postId);

    Optional<Post> findById(long postId);

    void createPost(Post p);

    void updatePost(Post p);

    void deletePost(long postId);
}
