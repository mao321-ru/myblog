package org.example.mbg.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Builder
@Data
public class PostShowDto {
    private final Long postId;
    private final String title;
    private final String text;
    private final String tags;
    private final boolean isImage;
    private final int likesCount;
    @Builder.Default List<CommentDto> comments = new ArrayList<>();
    private final LocalDateTime createTime;

    public String getPreviewText() {
        return text;
    }
}
