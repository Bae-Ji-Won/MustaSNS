package com.likelionfinalproject1.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    DUPLICATED_USER_NAME(HttpStatus.CONFLICT, "User name is duplicated."),  // 회원가입 아이디 중복검사할때 에러
    NOT_FOUND(HttpStatus.NOT_FOUND,"Not Found"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED,"Long Password"),       // 로그인할때 에러(비밀번호 틀림)
    USERNAME_NOTFOUND(HttpStatus.NOT_FOUND,"ID Not Found");                 // 로그인할때 에러(아이디 없음)

    private HttpStatus httpStatus;
    private String message;
}
