package org.example.mbg.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import org.example.mbg.dto.PostCreateDto;
import org.example.mbg.service.PostService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log
@RequiredArgsConstructor
@Controller
@RequestMapping({ "/", "/posts"})
public class PostController {
    private final PostService service;

    @GetMapping
    public String findPosts(Model model) {
        var genTime = LocalDateTime.now();
        log.info( "generated: " + genTime.format( DateTimeFormatter.ofPattern( "dd.MM.yyyy HH:mm:ss")));
        var posts = service.findPosts();
        log.info( "posts: " + posts);
        model.addAttribute( "posts", posts);
        model.addAttribute( "generatedTime", genTime);
        return "index";
    }

    @PostMapping
    public String createPost(
           PostCreateDto post
    ) {
        service.createPost( post);
        return "redirect:/";
    }

    @GetMapping("/{postId}")
    public String showPost(@PathVariable("postId") long postId, Model model) {
        log.info( "show post: postId= " + postId);
        model.addAttribute( "generatedTime", LocalDateTime.now());
        return "index";
    }

}
