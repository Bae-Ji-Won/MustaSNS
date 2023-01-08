package com.likelionfinalproject1.Domain.dto.Post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Update, Delete에 대한 Response
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class PostChangeResponse {
    private String message;
    private Long postId;
    

    public static PostChangeResponse success(String str, Long id){
        return new PostChangeResponse(str,id);
    }
}
