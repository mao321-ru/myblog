package org.example.mbg.controller;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import org.example.mbg.dto.PostCreateDto;
import org.example.mbg.mapper.PostMapper;
import org.example.mbg.model.Post;
import org.example.mbg.service.PostService;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
        var posts =  service.findPosts( normalizedTags);
        model.addAttribute( "posts", posts);
        model.addAttribute( "generatedTime", LocalDateTime.now());
        return "index";
    }

    @PostMapping
    public String createPost( PostCreateDto post) {
        service.createPost( post);
        return "redirect:/";
    }

    @GetMapping("/{postId}")
    public String showPost(@PathVariable("postId") long postId, Model model) {
        log.info( "show post: postId= " + postId);
        model.addAttribute( "generatedTime", LocalDateTime.now());
        return "index";
    }

    @GetMapping("/{postId}/image")
    @ResponseBody
    public ResponseEntity<InputStreamResource> getPostImage(@PathVariable("postId") long postId) {
        Optional<Post.Image> optImg = service.findPostImage( postId);
        if ( optImg.isPresent()) {
            var img = optImg.get();
            return ResponseEntity.ok()
                    .contentLength( img.getFileData().length)
                    .contentType(MediaType.parseMediaType( img.getContentType()))
                    .body( new InputStreamResource( new ByteArrayInputStream( img.getFileData())));
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

}
