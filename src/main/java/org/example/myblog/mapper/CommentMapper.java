package org.example.myblog.mapper;

import org.example.myblog.dto.*;
import org.example.myblog.model.Comment;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment cm) {
        return CommentDto.builder()
                .commentId( cm.getCommentId())
                .commentText( cm.getCommentText())
                .build();
    }
}
