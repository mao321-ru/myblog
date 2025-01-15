package org.example.mbg.repository;

import org.example.mbg.model.Post;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public class JdbcNativePostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativePostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<Post> findAll() {
        return jdbcTemplate.query(
                "select id, title, text, like_count from posts order by id",
                (rs, rowNum) -> Post.builder()
                        .id( rs.getLong("id"))
                        .title( rs.getString("title"))
                        .text( rs.getString("text"))
                        .likeCount( rs.getInt("like_count"))
                        .build()
                );
    }
}
