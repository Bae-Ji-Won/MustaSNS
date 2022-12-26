package com.likelionfinalproject1.Service;

import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.dto.Post.PostCreateRequest;
import com.likelionfinalproject1.Domain.dto.Post.PostCreateResponse;
import com.likelionfinalproject1.Domain.dto.Post.PostListResponse;
import com.likelionfinalproject1.Domain.dto.Post.PostOneResponse;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;

    // 포스트 새로 작성
    public PostCreateResponse postcreate(PostCreateRequest postCreateRequest, String userName){
        User user = userService.getUserByUserName(userName);    // 토큰에서 추출한 userName을 통해 User DB에서 해당 데이터 가져옴

        Post post = postRepository.save(postCreateRequest.toEntity(user));  // Post DB에 유저가 입력한 제목,내용 저장

        if(post == null){
            return PostCreateResponse.error();      // DB에서 오류가 발생하여 post값을 받아오지 못하면 "저장실패" 오류 출력
        }else {
            return PostCreateResponse.success(post.getId());    // 정상동작시 "등록 완료" 출력
        }
    }

    // 포스트 한개만 호출하기
    public PostOneResponse getpostbyid(Long id){
        Post post = postRepository.findById(id)         // 현재 post 번호를 통해 데이터를 가져온다
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        return new PostOneResponse().fromEntity(post,post.getUserId().getUserName());

    }

    // 포스트 리스트 모두 호출
    public Page<PostListResponse> findAllByPage(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);        // 모든 데이터 호출
        Page<PostListResponse> postListResponses = new PostListResponse().toDtoList(posts);     // Page<Entity> -> Page<Dto>변경
        return postListResponses;
    }

}
