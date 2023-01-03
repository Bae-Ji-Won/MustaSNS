package com.likelionfinalproject1.Domain.dto.Comment;

import com.likelionfinalproject1.Domain.Entity.Comment;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String comment;

    private String userName;

    private Long postId;

    private String createdAt;


    public CommentDto fromentity(Comment comment){
        return CommentDto.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .userName(comment.getUser().getUserName())
                .postId(comment.getPost().getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
