package org.example.mbg.mapper;

import lombok.SneakyThrows;
import org.example.mbg.dto.PostCreateDto;
import org.example.mbg.dto.PostPreviewDto;
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

    public static PostPreviewDto toPostPreviewDto(Post p) {
        return PostPreviewDto.builder()
                .postId( p.getPostId())
                .title( p.getTitle())
                .previewText( p.getText())
                .tags( p.getTags())
                .isImage( p.getImage() != null && p.getImage().getOrigFilename() != null && ! p.getImage().getOrigFilename().isEmpty())
                .createTime( p.getCreateTime())
                .likeCount( p.getLikeCount())
                // TODO не реализовано
                .commentCount( 0)
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
