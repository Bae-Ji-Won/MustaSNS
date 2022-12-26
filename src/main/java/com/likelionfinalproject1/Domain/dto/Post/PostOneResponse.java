package com.likelionfinalproject1.Domain.dto.Post;

import com.likelionfinalproject1.Domain.Entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

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
    private Timestamp createAt;
    private Timestamp lastModifiedAt;

    public PostOneResponse fromEntity(Post post, String userName){
        return PostOneResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .userName(userName)
                .createAt(post.getRegisteredAt())
                .lastModifiedAt(post.getUpdatedAt())
                .build();
    }
}
