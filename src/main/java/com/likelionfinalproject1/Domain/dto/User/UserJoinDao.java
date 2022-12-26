package com.likelionfinalproject1.Domain.dto.User;

import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class UserJoinDao {
    private Long id;
    private String username;
    private String password;
    private UserRole role;

    public static UserJoinDao fromEntity(User userEntity){
        UserJoinDao userDao = UserJoinDao.builder()
                .id(userEntity.getId())
                .username(userEntity.getUserName())
                .password(userEntity.getPassword())
                .role(userEntity.getRole())
                .build();

        return userDao;
    }
}
