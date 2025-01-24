package org.example.mbg.controller;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import org.example.mbg.dto.PostCreateDto;
import org.example.mbg.dto.PostUpdateDto;
import org.example.mbg.mapper.PostMapper;
import org.example.mbg.model.Post;
import org.example.mbg.service.PostService;

import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Log
@RequiredArgsConstructor
@Controller
@RequestMapping({ "/", "/posts"})
public class PostController {

    private final int DEFAULT_PAGE_SIZE = 50;

    private final PostService service;

    @GetMapping
    public String findPosts(
        @RequestParam( required = false) String tags,
        @RequestParam Optional<Integer> page,
        @RequestParam Optional<Integer> psize,
        Model model
    ) {
        int currentPage = page.filter( n -> n > 0).orElse( 1);
        int pageSize = psize.filter( n -> n > 0).orElse( DEFAULT_PAGE_SIZE);
        //log.info( "tags: " + tags + ", currentPage: " + currentPage + ", pageSize: " + pageSize);

        String normalizedTags = PostMapper.normalizeTags( tags);
        model.addAttribute( "normalizedTags", normalizedTags);

        var posts =  service.findPosts( normalizedTags, PageRequest.of( currentPage - 1, pageSize));
        model.addAttribute( "posts", posts);

        int totalPages = ( posts.getNumber() + 1) + ( posts.hasNext() ? 1 : 0);
        if( totalPages > 1 ) {
            var pageNumbers = IntStream.rangeClosed( 1, totalPages).boxed().toList();
            model.addAttribute( "pageNumbers", pageNumbers);
            model.addAttribute( "hasNextPage", posts.hasNext());
        }

        model.addAttribute( "generatedTime", LocalDateTime.now());

        return "index";
    }

    @PostMapping()
    public String createPost( PostCreateDto post) {
        service.createPost( post);
        return "redirect:/";
    }

    @GetMapping("/{postId}")
    public String showPost(@PathVariable("postId") long postId, Model model) {
        //log.info( "show post: postId= " + postId);
        var post = service.getPost( postId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found")
        );
        model.addAttribute( "post", post);
        model.addAttribute( "generatedTime", LocalDateTime.now());
        return "post";
    }

    @PostMapping("/{postId}")
    public String updatePost(
        PostUpdateDto post
    ) {
        //log.info( "update post:  dto: " + post);
        service.updatePost( post);
        return "redirect:/posts/" + post.getPostId();
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
