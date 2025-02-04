package org.example.myblog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.myblog.repository.CommentRepository;
import org.springframework.stereotype.Service;

@Service
@Log
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository cmtRepo;

    @Override
    public void createComment(long postId, String commentText) {
        cmtRepo.createComment( postId, commentText);
    }

    @Override
    public void updateComment(long postId, long commentId, String commentText) {
        cmtRepo.updateComment( postId, commentId, commentText);
    }

    @Override
    public void deleteComment(long postId, long commentId) {
        cmtRepo.deleteComment( postId, commentId);
    }

}
