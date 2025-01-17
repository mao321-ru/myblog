package org.example.mbg.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import org.example.mbg.service.PostService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Log
@RequiredArgsConstructor
@Controller
@RequestMapping({ "/", "/posts"})
public class PostController {
    private final PostService service;

    @GetMapping
    public String findPosts(Model model) {
        var genTime = LocalDateTime.now();
        log.info( "request time: " + genTime.format( DateTimeFormatter.ofPattern( "dd.MM.yyyy HH:mm:ss")));
        model.addAttribute( "posts", service.findPosts());
        model.addAttribute( "generatedTime", genTime);
        return "index";
    }

    @PostMapping
    public String savePost(
        String title,
        @RequestParam( required = false) String tags,
        @RequestParam( required = false) String text,
        @RequestParam( required = false) MultipartFile file
    ) {
        log.info( "title: " + title + ", tags: " + tags + ", text: " + text);
        if ( file != null && !file.isEmpty()) {
            log.info("file: " + file.getOriginalFilename() + ", size: " + file.getSize());
        }
        return "redirect:/";
    }

    @GetMapping("/{postId}")
    public String showPost(@PathVariable("postId") long postId, Model model) {
        log.info( "show post: postId= " + postId);
        model.addAttribute( "generatedTime", LocalDateTime.now());
        return "index";
    }

}
