package com.likelionfinalproject1.Controller;

import com.likelionfinalproject1.Domain.Response;
import com.likelionfinalproject1.Domain.dto.Post.*;
import com.likelionfinalproject1.Service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    // 포스트 작성하기
    @PostMapping
    public Response<PostCreateResponse> postCreate(@RequestBody PostCreateRequest postCreateRequest,Authentication authentication){
        String userName = authentication.getName();     // 토큰에서 userName 추출
        return Response.success(postService.postcreate(postCreateRequest,userName));
    }

    // 포스트 세부 게시물 출력
    @GetMapping("/{postsId}")       //현재 게시물 번호 받아옴
    public Response<PostOneResponse> getPostById(@PathVariable(name = "postsId") Long postId){
        return Response.success(postService.getpostbyid(postId));
    }

    // 포스트 리스트(전체 리스트)
    @GetMapping
    public Response<Page<PostListResponse>> getPostList(Pageable pageable){
        Page<PostListResponse> postpage = postService.findAllByPage(pageable);
        return Response.success(postpage);
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public Response<PostChangeResponse> postUpdate(@PathVariable Long id, @RequestBody PostCreateRequest request, Authentication authentication){
        String userName = authentication.getName();
        return Response.success(postService.postupdate(id,request,userName));
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public Response<PostChangeResponse> postDelete(@PathVariable Long id,Authentication authentication){
        String userName = authentication.getName();
        return Response.success(postService.postdelete(id,userName));
    }
}
