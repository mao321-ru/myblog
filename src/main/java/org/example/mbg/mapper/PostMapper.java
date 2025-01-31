package org.example.mbg.mapper;

import lombok.SneakyThrows;
import org.example.mbg.dto.PostCreateDto;
import org.example.mbg.dto.PostPreviewDto;
import org.example.mbg.dto.PostShowDto;
import org.example.mbg.dto.PostUpdateDto;
import org.example.mbg.model.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class PostMapper {

    @SneakyThrows
    public static Post toPost(PostCreateDto pd) {
        MultipartFile f = pd.getFile();
        return Post.builder()
                .title( pd.getTitle())
                .tags( normalizeTags( pd.getTags()))
                .text( pd.getText())
                .image(
                    f == null || f.isEmpty() ? null
                        : Post.Image.builder()
                            .origFilename( f.getOriginalFilename())
                            .contentType( f.getContentType())
                            .fileData( f.getBytes())
                            .build()
                )
                .build();
    }

    @SneakyThrows
    public static Post toPost(PostUpdateDto pd) {
        MultipartFile f = pd.getFile();
        return Post.builder()
                .postId( pd.getPostId())
                .title( pd.getTitle())
                .tags( normalizeTags( pd.getTags()))
                .text( pd.getText())
                .image(
                        // null указывает на удаление изображения
                        pd.getDelImage() != null && pd.getDelImage() ? null
                        : f == null || f.isEmpty() ? Post.Image.builder().build()
                        : Post.Image.builder()
                            .origFilename( f.getOriginalFilename())
                            .contentType( f.getContentType())
                            .fileData( f.getBytes())
                            .build()
                )
                .build();
    }

    // Возвращает текст превью: максимум 3 строки первого абзаца
    public static String getPreviewText( String text) {
        final int MAX_PREVIEW_LEN = 80 * 3;
        final char PARAGRAPH_SEPARATOR = '\n';

        boolean isMark = false;
        int previewLen = -1;
        if ( text != null) {
            previewLen = text.indexOf( PARAGRAPH_SEPARATOR);
            if ( previewLen > MAX_PREVIEW_LEN || previewLen == -1 && text.length() > MAX_PREVIEW_LEN) {
                previewLen = MAX_PREVIEW_LEN;
                isMark = true;
            }
        }
        return previewLen == -1
                ? text
                : text.substring( 0, previewLen) + ( isMark ? "..." : "");
    }

    public static PostPreviewDto toPostPreviewDto(Post p) {
        return PostPreviewDto.builder()
                .postId( p.getPostId())
                .title( p.getTitle())
                .previewText( getPreviewText( p.getText()))
                .tags( p.getTags())
                .isImage( p.getImage() != null && p.getImage().getOrigFilename() != null && ! p.getImage().getOrigFilename().isEmpty())
                .likesCount( p.getLikesCount())
                .commentsCount( p.getCommentsCount())
                .createTime( p.getCreateTime())
                .build();
    }

    public static PostShowDto toPostShowDto(Post p) {
        return PostShowDto.builder()
                .postId( p.getPostId())
                .title( p.getTitle())
                .text( p.getText())
                .tags( p.getTags())
                .isImage( p.getImage() != null && p.getImage().getOrigFilename() != null && ! p.getImage().getOrigFilename().isEmpty())
                .likesCount( p.getLikesCount())
                .createTime( p.getCreateTime())
                .build();
    }

    public static String normalizeTags(String tags) {
        return Optional.ofNullable( tags)
                .map( s ->
                    Arrays.stream(
                        // получаем строку тегов с разделитем пробел
                        s.replaceAll( "\\s+", " ")
                            .strip()
                            .split( " ")
                    )
                    // убираем дублирующие теги с сохранением их порядка
                    .distinct()
                    .collect(Collectors.joining( " "))
                )
                .orElse( "");
    }
}
