package com.likelionfinalproject1.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 오류든 정상이든 결과 상태를 데이터와 함께 반환하기 위한 클래스
@AllArgsConstructor
@Getter
public class Response <T>{
    private String resultCode;
    private T result;

    // 에러일 경우 에로 코드와 null값을 반환함
    public static <T> Response<T> error(T result){
        return new Response("ERROR",result);
    }

    // Controller에서 반환할때 한번 감싸서 반환해줌(성공했다는 메시지와 result 결과 데이터와 같이)
    public static <T> Response<T> success(T result){
        return new Response("SUCCESS",result);      // resultCode에는 성공이라는 메시지와 result라는 결과 데이터를 반환함
    }
}
