package org.example.mbg.repository;

import org.example.mbg.model.Comment;

import java.util.List;

public interface CommentRepository {
    List<Comment> findByPostId(long postId);

    public void createComment(long postId, String commentText);

    void updateComment(long postId, long commentId, String commentText);

    void deleteComment(long postId, long commentId);
}
