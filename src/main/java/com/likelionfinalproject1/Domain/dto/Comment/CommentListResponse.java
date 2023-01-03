package com.likelionfinalproject1.Domain.dto.Comment;

import com.likelionfinalproject1.Domain.Entity.Comment;
import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.dto.Post.PostListResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
public class CommentListResponse {
    private Long id;
    private String comment;

    private String userName;

    private Long postId;

    private String createdAt;

    public Page<CommentListResponse> commenttoDtoList(Page<Comment> commentList){
        // Page<Post>에 저장된 값들을 Page<PostListResponse>형식으로 변환함
        // 이때 m -> PostListResponse.builder()를 통해 모든 값을 순차적으로 변환함
        Page<CommentListResponse> commentDtoList = commentList.map(c -> CommentListResponse.builder()
                .id(c.getId())
                .comment(c.getComment())
                .userName(c.getUser().getUserName())
                .postId(c.getPost().getId())
                .createdAt(c.getCreatedAt())
                .build());
        return commentDtoList;
    }
}
