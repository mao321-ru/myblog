package org.example.mbg.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class Comment {
    private Long commentId;
    private Long postId;
    private String commentText;
}
