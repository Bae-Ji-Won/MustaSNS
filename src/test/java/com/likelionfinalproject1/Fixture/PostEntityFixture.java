package com.likelionfinalproject1.Fixture;

import com.likelionfinalproject1.Domain.Entity.Post;

// 임의 Entity Response 값
public class PostEntityFixture {
    public static Post postEntity(String userName,String password){
        Post post = Post.builder()
                .id(1l)
                .user(UserEntityFixture.userEntity(userName,password))
                .title("title")
                .body("body")
                .build();
        return post;
    }
}
