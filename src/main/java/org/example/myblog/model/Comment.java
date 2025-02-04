package org.example.myblog.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Comment {
    private Long commentId;
    private Long postId;
    private String commentText;
}
