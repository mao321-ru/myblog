package org.example.mbg.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.mbg.dto.*;
import org.example.mbg.mapper.CommentMapper;
import org.example.mbg.mapper.PostMapper;
import org.example.mbg.model.Post;
import org.example.mbg.repository.CommentRepository;
import org.example.mbg.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository repo;
    private final CommentRepository cmtRepo;

    @Override
    public Page<PostPreviewDto> findPosts(String tags, Pageable pageable) {
        Page<Post> posts = ( tags == null || tags.equals( "")
                ? repo.findAll( pageable)
                : repo.findByTags( tags, pageable));
        return posts.map( PostMapper::toPostPreviewDto);
    }

    @Override
    public Optional<Post.Image> findPostImage(long postId) {
        return repo.findPostImage( postId);
    }

    @Override
    public Optional<PostShowDto> getPost(long postId) {
        Optional<PostShowDto> po = repo.findById( postId).map( PostMapper::toPostShowDto);
        po.ifPresent(
            p -> p.setComments(
                cmtRepo.findByPostId( postId).stream()
                    .map(CommentMapper::toCommentDto)
                    .toList()
            )
        );
        return po;
    }

    @Override
    public void createPost(PostCreateDto post) {
        repo.createPost( PostMapper.toPost( post));
    }

    @Override
    public void updatePost(PostUpdateDto post) {
        repo.updatePost( PostMapper.toPost( post));
    }

    @Override
    public void deletePost(long postId) {
        repo.deletePost( postId);
    }

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
