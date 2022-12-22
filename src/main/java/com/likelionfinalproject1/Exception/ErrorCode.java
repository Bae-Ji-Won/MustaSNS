package com.likelionfinalproject1.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    DUPLICATED_USER_NAME(HttpStatus.CONFLICT, "UserName이 중복됩니다."),  // 회원가입 아이디 중복검사할때 에러
    USERNAME_NOTFOUND(HttpStatus.NOT_FOUND,"Not founded"),                 // 로그인할때 에러(아이디 없음)
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED,"패스워드가 잘못되었습니다."),       // 로그인할때 에러(비밀번호 틀림)
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,"잘못된 토큰입니다."),       // 로그인할때 에러(비밀번호 틀림)
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "사용자가 권한이 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 포스트가 없습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB에러");
    private HttpStatus httpStatus;
    private String message;
}
