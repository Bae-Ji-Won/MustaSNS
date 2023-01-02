package com.likelionfinalproject1.Domain.dto.Post;

import com.likelionfinalproject1.Domain.Entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

// 게시물 출력 DTO
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PostOneResponse {
    private Long id;
    private String title;
    private String body;
    private String userName;
    private String createdAt;
    private String lastModifiedAt;

    public PostOneResponse fromEntity(Post post, String userName){
        return PostOneResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .userName(userName)
                .createdAt(post.getCreatedAt())
                .lastModifiedAt(post.getLastModifiedAt())
                .build();
    }
}
