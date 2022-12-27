package com.likelionfinalproject1.Domain.dto.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// 게시물 생성(데이터 출력) DTO
@AllArgsConstructor
@Getter
@Builder
public class PostCreateResponse {
    private String message;
    private Long postId;

    // 에러일 경우 에로 코드와 null값을 반환함
    public static PostCreateResponse error(){
        return new PostCreateResponse("포스트 등록 실패",null);
    }

    public static PostCreateResponse success(Long id){
        return new PostCreateResponse("포스트 등록 완료",id);
    }

}
