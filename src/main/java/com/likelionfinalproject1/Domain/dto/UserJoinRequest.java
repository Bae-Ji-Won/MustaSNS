package com.likelionfinalproject1.Domain.dto;

import com.likelionfinalproject1.Domain.UserEntity;
import com.likelionfinalproject1.Domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserJoinRequest {
    private String userName;
    private String password;

    // 유저에게 입력받은 값을 UserEntity에 저장함
    public UserEntity toEntity(String pwd){
        UserEntity userEntity = UserEntity.builder()
                .userName(this.userName)
                .password(pwd)
                .role(UserRole.USER)           // 관리자를 제외한 회원은 모두 일반회원이므로 default값으로 저장
                .build();

        return userEntity;
    }
}
