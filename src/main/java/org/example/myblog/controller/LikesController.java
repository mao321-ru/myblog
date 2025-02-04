package org.example.myblog.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.myblog.service.LikesService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Log
@RequiredArgsConstructor
@Controller
public class LikesController {

    private final LikesService service;

    @PostMapping("/posts/{postId}/add-like")
    public String addLike( @PathVariable long postId) {
        service.addLikes( postId, 1);
        return "redirect:/posts/" + postId;
    }

}
