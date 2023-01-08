package com.likelionfinalproject1.Domain.dto.Post;

import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 게시물 생성(데이터 입력) DTO
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class PostCreateRequest {
    private String title;
    private String body;


    public Post toEntity(User user){
        return Post.builder()
                .title(this.title)
                .body(this.body)
                .user(user)
                .build();
    }
}
