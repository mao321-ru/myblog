package org.example.mbg.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import org.example.mbg.dto.PostCreateDto;
import org.example.mbg.mapper.PostMapper;
import org.example.mbg.service.PostService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Log
@RequiredArgsConstructor
@Controller
@RequestMapping({ "/", "/posts"})
public class PostController {
    private final PostService service;

    @GetMapping
    public String findPosts(
        @RequestParam( required = false) String tags,
        Model model
    ) {
        log.info( "tags: " + tags);
        String normalizedTags = PostMapper.normalizeTags( tags);
        model.addAttribute( "normalizedTags", normalizedTags);
        model.addAttribute( "posts", service.findPosts( normalizedTags));
        model.addAttribute( "generatedTime", LocalDateTime.now());
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
