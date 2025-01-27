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

    @Override
    public void createComment(long postId, String commentText) {
        jdbcTemplate.update( "insert into post_comments ( post_id, comment_text) values ( ?, ?)", postId, commentText);
    }

    @Override
    public void updateComment(long postId, long commentId, String commentText) {
        jdbcTemplate.update(
            """
                update
                    post_comments
                set
                    comment_text = ?
                where
                    comment_id = ?
                    and post_id = ?
                """,
                commentText,
                commentId,
                postId
        );
    }

    @Override
    public void deleteComment(long postId, long commentId) {
        jdbcTemplate.update(
                """
                    delete from
                        post_comments
                    where
                        comment_id = ?
                        and post_id = ?
                    """,
                commentId,
                postId
        );
    }
}
