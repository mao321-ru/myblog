package org.example.myblog.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.myblog.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Log
@RequiredArgsConstructor
@Controller
public class CommentController {

    private final CommentService service;

    @PostMapping("/posts/{postId}/comments")
    public String createComment( @PathVariable long postId, String commentText) {
        //log.info( "create comment: postId:  " + postId + ", commentText: " + commentText);
        service.createComment( postId, commentText);
        return "redirect:/posts/" + postId;
    }

    @PostMapping(value = "/posts/{postId}/comments/{commentId}", params = "_method=")
    public String updateComment( @PathVariable long postId, @PathVariable long commentId, String commentText) {
        //log.info( "update comment: postId:  " + postId + ", commentId: " + commentId + ", commentText: " + commentText);
        service.updateComment( postId, commentId, commentText);
        return "redirect:/posts/" + postId;
    }

    @PostMapping(value = "/posts/{postId}/comments/{commentId}", params = "_method=delete")
    public String deleteComment( @PathVariable long postId, @PathVariable long commentId) {
        //log.info( "delete comment: postId:  " + postId + ", commentId: " + commentId);
        service.deleteComment( postId, commentId);
        return "redirect:/posts/" + postId;
    }

}