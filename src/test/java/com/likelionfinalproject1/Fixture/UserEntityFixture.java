package com.likelionfinalproject1.Fixture;

import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.UserRole;

// 임의 Entity Response 값
public class UserEntityFixture {
    public static User userEntity(String userName, String password){
        User user = User.builder()
                .id(1l)
                .userName(userName)
                .password(password)
                .role(UserRole.USER)
                .build();

        return user;
    }
}
