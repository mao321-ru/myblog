package org.example.mbg.mapper;

import lombok.SneakyThrows;
import org.example.mbg.dto.*;
import org.example.mbg.model.Comment;
import org.example.mbg.model.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment cm) {
        return CommentDto.builder()
                .commentId( cm.getCommentId())
                .commentText( cm.getCommentText())
                .build();
    }
}
