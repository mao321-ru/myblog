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
                "select post_id, title, text, like_count, create_time from posts order by post_id desc",
                (rs, rowNum) -> Post.builder()
                        .postId( rs.getLong("post_id"))
                        .title( rs.getString("title"))
                        .text( rs.getString("text"))
                        .likeCount( rs.getInt("like_count"))
                        .createTime( rs.getTimestamp( "create_time").toLocalDateTime())
                        .build()
                );
    }

    @Override
    public void createPost(Post p) {
        jdbcTemplate.update(
                "insert into posts ( title, text) values ( ?, ?)",
                p.getTitle(),
                p.getText()
        );
    }
}
