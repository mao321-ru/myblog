package org.example.mbg.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.mbg.model.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Log
@RequiredArgsConstructor
public class JdbcNativeCommentRepository implements CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Comment> findByPostId(long postId) {
        return jdbcTemplate.query(
                """
                select
                    t.comment_id,
                    t.post_id,
                    t.comment_text
                from
                    post_comments t
                where
                    t.post_id = ?
                """,
                ( rs, rownum ) -> {
                    return Comment.builder()
                            .commentId(rs.getLong("comment_id"))
                            .postId(rs.getLong("post_id"))
                            .commentText(rs.getString("comment_text"))
                            .build();
                },
                postId
            );
    }
}
