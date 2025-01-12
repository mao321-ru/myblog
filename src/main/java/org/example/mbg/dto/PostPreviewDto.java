package org.example.mbg.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


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
}
