package com.likelionfinalproject1.Domain.dto.Post;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Update, Delete에 대한 Response
@AllArgsConstructor
@Getter
public class PostChangeResponse {
    private String message;
    private Long postId;
    

    public static PostChangeResponse success(String str, Long id){
        return new PostChangeResponse(str,id);
    }
}
