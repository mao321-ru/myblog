package org.example.mbg.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcNativeLikesRepository implements LikesRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLikes(long postId, int i) {
        jdbcTemplate.update(
                """
                update
                    posts p
                set
                    likes_count = coalesce( p.likes_count, 0) + ?
                where
                    p.post_id = ?
                """,
                i,
                postId
        );
    }

}
