package org.example.mbg.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.mbg.model.Post;

import java.time.LocalDateTime;
import java.util.stream.Collectors;


@Builder
@Getter
public class PostPreviewDto {
    private final Long postId;
    private final String title;
    private final String previewText;
    private final String previewTags;
    private final LocalDateTime createTime;
    private final int likeCount;
    private final int commentCount;

    public static PostPreviewDto fromModel(Post o) {
        return builder()
            .postId( o.getId())
            .title( o.getTitle())
            .previewText( o.getText())
            .previewTags(
                o.getTags().stream()
                    .map( s -> "#" + s)
                    .collect( Collectors.joining(" "))
            )
            .createTime( o.getCreateTime())
            .likeCount( o.getLikeCount())
            // TODO не реализовано
            .commentCount( 0)
            .build();
    }
}
