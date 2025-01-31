package org.example.mbg.service;

import lombok.RequiredArgsConstructor;
import org.example.mbg.repository.LikesRepository;
import org.example.mbg.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikesServiceImpl implements LikesService {
    private final LikesRepository repo;

    @Override
    public void addLikes(long postId, int i) {
        repo.addLikes( postId, i);
    }

}
