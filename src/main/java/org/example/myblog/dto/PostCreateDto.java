package org.example.myblog.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostCreateDto {
    private final String title;
    private final String tags;
    private final String text;
    private final MultipartFile file;
}
