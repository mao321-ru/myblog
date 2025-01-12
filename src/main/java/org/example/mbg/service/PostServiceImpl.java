package org.example.mbg.service;

import org.example.mbg.dto.PostPreviewDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    @Override
    public List<PostPreviewDto> findPosts() {
        List<PostPreviewDto> posts = List.of(
            PostPreviewDto.builder()
                .postId( 1L)
                .title( "Байкал")
                .previewText( "Это замечательное озеро")
                .previewTags( "#nature #baikal")
                .createTime( LocalDateTime.parse( "2025-01-12T19:44:05"))
                .likeCount( 3)
                .commentCount( 2)
                .build(),
            PostPreviewDto.builder()
                .postId( 2L)
                .title( "Амур")
                .build()
        );
        return posts;
    }
}
