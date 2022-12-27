package com.likelionfinalproject1.Domain.dto.Post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PostUpdateResponse {
    private String message;
    private Long postId;
    

    public static PostUpdateResponse success(Long id){
        return new PostUpdateResponse("포스트 수정 완료",id);
    }
}
