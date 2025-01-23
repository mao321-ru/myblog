package org.example.mbg.repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.mbg.model.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Log
@RequiredArgsConstructor
public class JdbcNativePostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    private final LobHandler lobHandler;

    // Базовый SQL для получения данных поста и соответствующий маппер
    private String FIND_POST_BASE_SQL =
        """
        select
            p.post_id,
            p.title,
            p.tags_str as tags,
            p.text,
            pi.orig_filename as image_filename,
            p.likes_count,
            coalesce( cm.comments_count, 0) as comments_count,
            p.create_time
        from
            posts p
            left join post_images pi
                on pi.post_id = p.post_id
            left join
                (
                select
                    pc.post_id,
                    count(1) as comments_count
                from
                    post_comments pc
                group by
                    pc.post_id
                ) cm
                on cm.post_id = p.post_id
        """;
    private final RowMapper<Post> postRowMapper = ( rs, rownum ) -> {
        return Post.builder()
                .postId( rs.getLong("post_id"))
                .title( rs.getString("title"))
                .tags( rs.getString("tags"))
                .text( rs.getString("text"))
                .image( Post.Image.builder().origFilename( rs.getString( "image_filename")).build())
                .likesCount( rs.getInt("likes_count"))
                .commentsCount( rs.getInt("comments_count"))
                .createTime( rs.getTimestamp( "create_time").toLocalDateTime())
                .build();
    };

    @Override
    public Optional<Post> findById(long postId) {
        return jdbcTemplate.query( FIND_POST_BASE_SQL +
                """
                where
                    p.post_id = ?
                """,
                postRowMapper,
                postId
            ).stream().findFirst();
    }

    private Page<Post> toPage(List<Post> posts, Pageable page) {
        var size = posts.size();
        // убираем лишний элемент, который возвращался из БД для проверки наличия следующей страницы
        if ( size == page.getPageSize() + 1) {
            posts.removeLast();
        }
        return new PageImpl<Post>( posts, page, page.getPageSize() * page.getPageNumber() + size);
    }

    @Override
    public Page<Post> findAll(Pageable page) {
        return toPage(
            jdbcTemplate.query( FIND_POST_BASE_SQL +
                """
                order by
                    p.post_id desc
                offset ? rows fetch next ? rows only
                """,
                postRowMapper,
          page.getPageSize() * page.getPageNumber(),
                page.getPageSize() + 1
            ),
            page
        );
    }

    @Override
    public Page<Post> findByTags(String tags, Pageable page) {
        int tagsCount = tags.split( " ").length;
        return toPage(
            jdbcTemplate.query( FIND_POST_BASE_SQL +
                """
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
                offset ? rows fetch next ? rows only
                """,
                postRowMapper,
          tags,
                tagsCount,
                page.getPageSize() * page.getPageNumber(),
                page.getPageSize() + 1
            ),
            page
        );
    }

    @Override
    public Optional<Post.Image> findPostImage(long postId) {
        List<Post.Image> lst = jdbcTemplate.query(
                """
                select orig_filename, content_type, file_data from post_images where post_id = ?
                """,
                ( rs, rownum) -> {
                    return Post.Image.builder()
                            .origFilename( rs.getString( "orig_filename"))
                            .contentType( rs.getString( "content_type"))
                            .fileData( rs.getBytes( "file_data"))
                            .build();
                },
                postId
        );
        return lst.stream().findFirst();
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
        if( p.getImage() != null) {
            mergePostImage( p.getPostId(), p.getImage());
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

    private void mergePostImage(Long postId, Post.Image image) {
        byte[] fd = image.getFileData();
        jdbcTemplate.execute(
                """
                insert into
                    post_images
                (
                    post_id,
                    orig_filename,
                    content_type,
                    file_data
                )
                values
                (?, ?, ?, ?)
                """,
                new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
                    protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException {
                        ps.setLong(1, postId);
                        ps.setString(2, image.getOrigFilename());
                        ps.setString(3, image.getContentType());
                        lobCreator.setBlobAsBinaryStream( ps, 4, new ByteArrayInputStream( fd), fd.length);
                    }
                }
        );
    }
}
