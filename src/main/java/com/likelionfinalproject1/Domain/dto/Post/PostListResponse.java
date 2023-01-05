package com.likelionfinalproject1.Domain.dto.Post;

import com.likelionfinalproject1.Domain.Entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PostListResponse {

    private Long id;
    private String title;
    private String body;
    private String userName;
    private String createdAt;
    private String lastModifiedAt;
    
    public Page<PostListResponse> toDtoList(Page<Post> postList){
        // Page<Post>에 저장된 값들을 Page<PostListResponse>형식으로 변환함
        // 이때 m -> PostListResponse.builder()를 통해 모든 값을 순차적으로 변환함
        Page<PostListResponse> boardDtoList = postList.map(m -> PostListResponse.builder()
                .id(m.getId())          // m(Post)에서 데이터 가져와 PostListResponse에 데이터 삽입
                .title(m.getTitle())
                .body(m.getBody())
                .userName(m.getUser().getUserName())
                .createdAt(m.getCreatedAt())
                .lastModifiedAt(m.getLastModifiedAt())
                .build());
        return boardDtoList;
    }
}
