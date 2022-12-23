package com.likelionfinalproject1.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 회원을 구분하기 위한 enum 클래스(관리자와 일반회원 2개만 존재하므로 enum으로 미리 값 고정함)
@AllArgsConstructor
@Getter
public enum UserRole {
    ADMIN,USER
}
