package com.likelionfinalproject1.Domain.dto.Comment;

import com.likelionfinalproject1.Domain.Entity.Comment;
import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Getter
@NoArgsConstructor
public class CommentRequest {
    private String comment;

    public Comment toEntity(User user,Post post){
        return Comment.builder()
                .comment(this.comment)
                .post(post)
                .user(user)
                .build();
    }
}
