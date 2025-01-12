package org.example.mbg.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import org.example.mbg.service.PostService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        log.info( "request time: " + genTime.format( DateTimeFormatter.ofPattern( "dd.MM.yyyy HH:mm:ss")));
        model.addAttribute( "posts", service.findPosts());
        model.addAttribute( "generatedTime", genTime);
        return "index";
    }

    @GetMapping("/{postId}")
    public String showPost(@PathVariable("postId") long postId, Model model) {
        log.info( "show post: postId= " + postId);
        model.addAttribute( "generatedTime", LocalDateTime.now());
        return "index";
    }

}
