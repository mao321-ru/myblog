package org.example.myblog.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Builder
@Data
public class PostPreviewDto {
    private final Long postId;
    private final String title;
    private final String previewText;
    private final String tags;
    private final boolean isImage;
    private final int likesCount;
    private final int commentsCount;
    private final LocalDateTime createTime;
}
