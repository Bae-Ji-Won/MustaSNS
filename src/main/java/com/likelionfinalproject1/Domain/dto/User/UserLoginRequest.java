package com.likelionfinalproject1.Domain.dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class UserLoginRequest {
    private String userName;
    private String password;
}
