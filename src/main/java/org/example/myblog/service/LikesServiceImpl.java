package org.example.myblog.service;

import lombok.RequiredArgsConstructor;
import org.example.myblog.repository.LikesRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikesServiceImpl implements LikesService {
    private final LikesRepository repo;

    @Override
    public void addLikes(long postId, int i) {
        repo.addLikes( postId, i);
    }

}
