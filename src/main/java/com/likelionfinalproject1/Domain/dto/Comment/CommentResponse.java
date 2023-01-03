package com.likelionfinalproject1.Domain.dto.Comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private String comment;

    private String userName;

    private Long postId;

    private String createdAt;

    public static CommentResponse fromDto(CommentDto dto){
        return CommentResponse.builder()
                .id(dto.getId())
                .comment(dto.getComment())
                .userName(dto.getUserName())
                .postId(dto.getPostId())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}
