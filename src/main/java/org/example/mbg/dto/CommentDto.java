package org.example.mbg.dto;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class CommentDto {
    private final Long commentId;
    private final String commentText;
}
