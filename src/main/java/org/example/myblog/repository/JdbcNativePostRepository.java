package org.example.myblog.repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.myblog.model.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
@Log
@RequiredArgsConstructor
public class JdbcNativePostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

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
                select
                    t.orig_filename,
                    t.content_type,
                    t.file_data
                from
                    post_images t
                where
                    t.post_id = ?
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

    @Transactional
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

    @Transactional
    @Override
    public void updatePost(Post p) {
        jdbcTemplate.update(
                """
                update
                    posts p
                set
                    title = ?,
                    tags_str = ?,
                    text = ?
                where
                    p.post_id = ?
                """,
                p.getTitle(),
                p.getTags(),
                p.getText(),
                p.getPostId()
        );
        mergePostTags( p.getPostId(), p.getTags());
        Post.Image img = p.getImage();
        if( img == null || img != null && img.getOrigFilename() != null && ! img.getOrigFilename().isEmpty()) {
            mergePostImage( p.getPostId(), img);
        }
    }

    @Transactional
    @Override
    public void deletePost(long postId) {
        jdbcTemplate.update( "delete from post_comments where post_id = ?", postId);
        jdbcTemplate.update( "delete from post_images where post_id = ?", postId);
        jdbcTemplate.update( "delete from post_tags where post_id = ?", postId);
        jdbcTemplate.update( "delete from posts where post_id = ?", postId);
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
                    s.tag_name
                from
                    ( select string_to_table( ?, ' ') as tag_name) s
                group by
                    s.tag_name
                having
                    s.tag_name not in
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
        var img = image != null
                ? image
                : Post.Image.builder().origFilename("").contentType( "").fileData( new byte[]{}).build();
        byte[] fd = img.getFileData();
        jdbcTemplate.update(
                """
                merge into
                    post_images d
                using
                    (
                    select
                        *
                    from
                        (
                        select
                            ? as post_id,
                            ? as orig_filename,
                            ? as content_type,
                            ? as file_data
                        ) a
                    where
                        coalesce( length( a.orig_filename), 0) > 0
                    ) s
                on
                    d.post_id = s.post_id
                when not matched then
                    insert
                    (
                        post_id,
                        orig_filename,
                        content_type,
                        file_data
                    )
                    values
                    (
                        s.post_id,
                        s.orig_filename,
                        s.content_type,
                        s.file_data
                    )
                when matched then
                    update set
                        orig_filename = s.orig_filename,
                        content_type = s.content_type,
                        file_data = s.file_data
                when not matched by source and
                        d.post_id = ?
                    then delete
                """,
                postId,
                img.getOrigFilename(),
                img.getContentType(),
                fd,
                postId
        );
    }
}
