package org.example.mbg.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.example.mbg.model.Post;

import java.time.LocalDateTime;
import java.util.stream.Collectors;


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
