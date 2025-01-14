package org.example.mbg.repository;

import org.example.mbg.model.Post;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public class InMemoryPostRepository implements PostRepository {
    @Override
    public List<Post> findAll() {
        List<Post> posts = List.of(
            Post.builder()
                    .id( 1L)
                    .title( "Байкал")
                    .text( "Это замечательное озеро")
                    .tags( List.of("nature", "baikal"))
                    .createTime( ZonedDateTime.parse( "2025-01-12T19:44:05+04:00"))
                    .likeCount( 3)
                    .build(),
            Post.builder()
                .id( 2L)
                .title( "Амур")
                .build()
        );
        return posts;
    }
}
