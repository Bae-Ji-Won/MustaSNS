package com.likelionfinalproject1.Domain.dto;

import com.likelionfinalproject1.Exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;


// 오류을 반환할때 에러코드와 메세지를 담아서 반환
@AllArgsConstructor
@Getter
public class ErrorResponse {
    private String errorCode;
    private String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.errorCode = errorCode.name();
        this.message = errorCode.getMessage();
    }

}
