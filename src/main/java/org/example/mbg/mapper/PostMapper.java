package org.example.mbg.mapper;

import org.example.mbg.dto.PostCreateDto;
import org.example.mbg.dto.PostPreviewDto;
import org.example.mbg.model.Post;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class PostMapper {

    public static Post toPost(PostCreateDto pd) {
        return Post.builder()
                .title( pd.getTitle())
                .tags(
                    Arrays.stream(
                            Optional.ofNullable( pd.getTags()).orElse( "")
                                .split("[#\\s]+")
                        )
                        .filter( s -> s.length() > 0)
                        .distinct()
                        .toList()
                )
                .text( pd.getText())
                // TODO use pd.getFile()
                .build();
    }

    public static PostPreviewDto toPostPreviewDto(Post p) {
        return PostPreviewDto.builder()
                .postId( p.getPostId())
                .title( p.getTitle())
                .previewText( p.getText())
                .previewTags(
                    p.getTags().stream()
                        .map( s -> "#" + s)
                        .collect( Collectors.joining(" "))
                )
                .createTime( p.getCreateTime())
                .likeCount( p.getLikeCount())
                // TODO не реализовано
                .commentCount( 0)
                .build();
    }

}
