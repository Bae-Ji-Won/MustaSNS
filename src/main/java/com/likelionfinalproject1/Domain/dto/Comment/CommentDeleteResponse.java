package com.likelionfinalproject1.Domain.dto.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class CommentDeleteResponse {
    private String message;
    private Long id;
}
