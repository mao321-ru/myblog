package org.example.mbg.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class Post {
    private Long postId;
    private String title;
    private String text;
    private String tags;
    private Image image;
    private int likesCount;
    private int commentsCount;
    @Builder.Default private LocalDateTime createTime = LocalDateTime.now();

    @Builder
    @Data
    public static class Image {
        private String origFilename;
        private String contentType;
        private byte[] fileData;
    }
}
