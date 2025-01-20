package org.example.mbg.repository;

import lombok.NonNull;
import org.example.mbg.model.Post;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository
public class JdbcNativePostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Post> postRowMapper = ( rs, rownum ) -> {
        return Post.builder()
                .postId( rs.getLong("post_id"))
                .title( rs.getString("title"))
                .tags( rs.getString("tags"))
                .text( rs.getString("text"))
                .likeCount( rs.getInt("like_count"))
                .createTime( rs.getTimestamp( "create_time").toLocalDateTime())
                .build();
    };

    public JdbcNativePostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Post> findAll() {
        return jdbcTemplate.query(
                """
                select
                    post_id,
                    title,
                    tags_str as tags, 
                    text, 
                    like_count, 
                    create_time
                from
                    posts p
                order by 
                    p.post_id desc
                """,
                postRowMapper
        );
    }

    @Override
    public List<Post> findByTags(String tags) {
        int tagsCount = tags.split( " ").length;
        return jdbcTemplate.query(
                """
                select
                    post_id,
                    title,
                    tags_str as tags,
                    text,
                    like_count,
                    create_time
                from
                    posts p
                where
                    p.post_id in
                        (
                        select
                            pt.post_id
                        from
                            ( select string_to_table( ?, ' ') as tag_name) p
                            join tags t
                                on t.tag_name = p.tag_name
                            join post_tags pt
                                on pt.tag_id = t.tag_id
                        group by
                            pt.post_id
                        having
                            count(1) = ?
                        )
                order by
                    p.post_id desc
                """,
                postRowMapper,
                new Object[]{ tags, tagsCount}
                );
    }

    @Override
    public void createPost(Post p) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
            con -> {
                PreparedStatement ps = con.prepareStatement(
                    "insert into posts ( title, tags_str, text) values ( ?, ?, ?)",
                    // указываем колонку с автогенеренным id для возврата через keyHolder
                    new String[]{ "post_id"}
                );
                ps.setString( 1, p.getTitle());
                ps.setString( 2, p.getTags());
                ps.setString( 3, p.getText());
                return ps;
            },
            keyHolder
        );
        p.setPostId( keyHolder.getKey().longValue());
        if( !p.getTags().isEmpty()) {
            mergePostTags( p.getPostId(), p.getTags());
        }
    }

    private void mergePostTags(@NonNull Long postId, String tags) {
        jdbcTemplate.update(
                """
                insert into
                    tags
                (
                    tag_name
                )
                select
                    tag_name
                from
                    ( select string_to_table( ?, ' ') as tag_name)
                group by
                    tag_name
                having
                    tag_name not in
                        (
                        select
                            t.tag_name
                        from
                            tags t
                        )
                order by
                    1
                """,
                tags
        );
        jdbcTemplate.update(
                """
                merge into
                    post_tags d
                using
                    (
                    select distinct
                        ? as post_id,
                        t.tag_id
                    from
                        (select string_to_table( ?, ' ') as tag_name) a
                        join tags t
                            on t.tag_name = a.tag_name
                    ) s
                on
                    d.post_id = s.post_id
                    and d.tag_id = s.tag_id
                when not matched then
                    insert
                    (
                        post_id,
                        tag_id
                    )
                    values
                    (
                        s.post_id,
                        s.tag_id
                    )
                when not matched by source and
                    d.post_id = ?
                    then delete
                """,
                postId,
                tags,
                postId
        );
    }
}
