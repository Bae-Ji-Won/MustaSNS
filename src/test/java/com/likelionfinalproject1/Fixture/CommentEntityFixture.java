package com.likelionfinalproject1.Fixture;

import com.likelionfinalproject1.Domain.Entity.Comment;


// 임의 Entity Response 값
public class CommentEntityFixture {
    public static Comment commentEntity(String userName, String password){
        return Comment.builder()
                .id(1l)
                .comment("comment test!!")
                .post(PostEntityFixture.postEntity(userName,password))
                .user(UserEntityFixture.userEntity(userName,password))
                .build();
    }
}
