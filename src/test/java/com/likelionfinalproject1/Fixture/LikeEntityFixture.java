package com.likelionfinalproject1.Fixture;

import com.likelionfinalproject1.Domain.Entity.Like;


// 임의 Entity Response 값
public class LikeEntityFixture {

    public static Like likeEntity(String userName, String password){
        Like like = Like.builder()
                .id(1l)
                .post(PostEntityFixture.postEntity(userName,password))
                .user(UserEntityFixture.userEntity(userName,password))
                .build();
        return like;
    }
}
