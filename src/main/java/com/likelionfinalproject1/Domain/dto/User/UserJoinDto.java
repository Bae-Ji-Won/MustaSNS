package com.likelionfinalproject1.Domain.dto.User;

import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class UserJoinDto {
    private Long id;
    private String username;
    private String password;
    private UserRole role;

    public static UserJoinDto fromEntity(User userEntity){
        UserJoinDto userDao = UserJoinDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUserName())
                .password(userEntity.getPassword())
                .role(userEntity.getRole())
                .build();

        return userDao;
    }
}
