package org.example.mbg.service;

public interface CommentService {
    void createComment( long postId, String commentText);

    void updateComment(long postId, long commentId, String commentText);

    void deleteComment(long postId, long commentId);

}
