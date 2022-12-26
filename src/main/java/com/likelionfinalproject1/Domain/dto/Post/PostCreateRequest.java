package com.likelionfinalproject1.Domain.dto.Post;

import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

// 게시물 생성(데이터 입력) DTO
@AllArgsConstructor
@Getter
public class PostCreateRequest {
    private String title;
    private String body;


    public Post toEntity(User user){
        return Post.builder()
                .title(this.title)
                .body(this.body)
                .userId(user)
                .build();
    }
}
