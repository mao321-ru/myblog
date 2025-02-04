package org.example.myblog.repository;

import org.example.myblog.model.Comment;

import java.util.List;

public interface CommentRepository {
    List<Comment> findByPostId(long postId);

    public void createComment(long postId, String commentText);

    void updateComment(long postId, long commentId, String commentText);

    void deleteComment(long postId, long commentId);
}
