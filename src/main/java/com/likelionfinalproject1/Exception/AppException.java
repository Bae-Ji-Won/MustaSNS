package com.likelionfinalproject1.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

// RuntimeException을 상속받는 사용자 정의 AppException의 에러를 생성함
@Getter
@AllArgsConstructor
public class AppException extends RuntimeException{
    private ErrorCode errorCode;
    private String message;

    public AppException(ErrorCode errorCode){       // 에러만 받고 메세지는 받지 않는 경우
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }

    @Override
    public String toString(){
        if(message == null)
            return errorCode.getMessage();

        return String.format("%s. %s",errorCode.getMessage(),message);

    }

}
