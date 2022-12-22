package com.likelionfinalproject1.Domain.dto;

import com.likelionfinalproject1.Domain.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserLoginRequest {
    private String userName;
    private String password;
}
