package org.example.mbg.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Post {
    private Long id;
    private String title;
    private String text;
    @Builder.Default private List<String> tags = new ArrayList<>();
    @Builder.Default private LocalDateTime createTime = LocalDateTime.now();
    private int likeCount;
}
