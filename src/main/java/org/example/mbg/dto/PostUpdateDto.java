package org.example.mbg.dto;

import lombok.Data;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostUpdateDto {
    private final Long postId;
    private final String title;
    private final String tags;
    private final String text;
    private final MultipartFile file;
    private final Boolean delImage;
}
