package com.likelionfinalproject1.Domain.dto.Mypage;

import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.dto.Post.PostListResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class MypagelistResponse {
    private Long id;
    private String title;
    private String body;
    private String userName;
    private String createdAt;

    public Page<MypagelistResponse> toDtoList(Page<Post> postList){
        // Page<Post>에 저장된 값들을 Page<MypagelistResponse>형식으로 변환함
        // 이때 m -> MypagelistResponse.builder()를 통해 모든 값을 순차적으로 변환함
        Page<MypagelistResponse> MyboardDtoList = postList.map(m -> MypagelistResponse.builder()
                .id(m.getId())          // m(Post)에서 데이터 가져와 MypagelistResponse에 데이터 삽입
                .title(m.getTitle())
                .body(m.getBody())
                .userName(m.getUser().getUserName())
                .createdAt(m.getCreatedAt())
                .build());
        return MyboardDtoList;
    }
}
