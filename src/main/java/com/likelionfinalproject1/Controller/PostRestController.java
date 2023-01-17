package com.likelionfinalproject1.Controller;

import com.likelionfinalproject1.Domain.Response;
import com.likelionfinalproject1.Domain.dto.Comment.*;
import com.likelionfinalproject1.Domain.dto.Mypage.MypagelistResponse;
import com.likelionfinalproject1.Domain.dto.Post.*;
import com.likelionfinalproject1.Service.CommentService;
import com.likelionfinalproject1.Service.LikeService;
import com.likelionfinalproject1.Service.PostService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;


@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Slf4j
public class PostRestController {

    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    // 포스트 작성하기
    @ApiOperation(value = "포스트 작성", notes = "유저에게 title,body입력받아 게시물 작성")
    @Transactional      // 에러 발생하면 롤백시켜 실행전 상태로 만든다.
    @PostMapping
    public Response<PostCreateResponse> postCreate(@RequestBody PostCreateRequest postCreateRequest,Authentication authentication){
        String userName = authentication.getName();     // 토큰에서 userName 추출
        return Response.success(postService.postCreate(postCreateRequest,userName));
    }

    // 포스트 세부 게시물 출력
    @ApiOperation(value = "포스트 세부 정보 출력", notes = "해당 포스트에 세부 내용 출력")
    @GetMapping("/{postsId}")       //현재 게시물 번호 받아옴
    public Response<PostOneResponse> getPostById(@PathVariable(name = "postsId") Long postId){
        return Response.success(postService.getpostbyid(postId));
    }

    // 포스트 리스트(전체 리스트)
    @ApiOperation(value = "포스트 List", notes = "Post DB에 저장된 모든 Post 데이터 출력")
    @GetMapping
    public Response<Page<PostListResponse>> getPostList(
            @PageableDefault(size = 20, sort = {"createdAt"}, direction = Sort.Direction.ASC)Pageable pageable){
        Page<PostListResponse> postpage = postService.findAllByPage(pageable);
        return Response.success(postpage);
    }

    // 게시글 수정
    @ApiOperation(value = "포스트 수정", notes = "기존의 포스트 정보를 새로운 내용으로 수정함")
    @PutMapping("/{id}")
    public Response<PostChangeResponse> postUpdate(@RequestBody PostCreateRequest request,@PathVariable Long id,  Authentication authentication){
        String userName = authentication.getName();
        return Response.success(postService.postupdate(id,request,userName));
    }

    // 게시글 삭제
    @ApiOperation(value = "포스트 삭제", notes = "기존의 저장되어 있는 포스트를 삭제함")
    @DeleteMapping("/{id}")
    public Response<PostChangeResponse> postDelete(@PathVariable Long id,Authentication authentication){
        String userName = authentication.getName();
        return Response.success(postService.postdelete(id,userName));
    }


    // ---------------------- 댓글 ---------------------

    // 댓글 작성
    @ApiOperation(value = "댓글 작성", notes = "유저가 원하는 포스트에 댓글 작성")
    @PostMapping("/{postsId}/comments")
    public Response<CommentResponse> commentCreate(@PathVariable(name = "postsId")Long id, @RequestBody CommentRequest request, Authentication auth){
        String userName = auth.getName();
        log.info("댓글작성 username :"+userName);
        CommentDto dto = commentService.commentCreate(id,request,userName);
        return Response.success(new CommentResponse().fromDto(dto));
    }

    // 댓글 리스트 전체 출력
    @ApiOperation(value = "댓글 List", notes = "해당 포스트에 대한 모든 댓글 출력")
    @GetMapping("/{postId}/comments")
    public Response<Page<CommentListResponse>> commentList(@PathVariable(name = "postId") Long id,
            @PageableDefault(size = 10, sort = {"createdAt"}, direction = Sort.Direction.ASC)Pageable pageable){
        Page<CommentListResponse> commentpage = commentService.findAllById(id,pageable);
        return Response.success(commentpage);
    }

    // 댓글 수정
    @ApiOperation(value = "댓글 수정", notes = "기존의 댓글을 수정함")
    @PutMapping("/{postId}/comments/{id}")
    public Response<CommentResponse> commentUpdate(@PathVariable(name = "postId")Long postid, @PathVariable(name="id")Long id,@RequestBody CommentRequest request, Authentication auth){
        String userName = auth.getName();
        CommentResponse response = commentService.commentUpdate(postid,id,request,userName);
        return Response.success(response);
    }

    // 댓글 삭제
    @ApiOperation(value = "댓글 삭제", notes = "기존의 댓글을 삭제함")
    @DeleteMapping("{postsId}/comments/{id}")
    public Response<CommentDeleteResponse> commentDelete(@PathVariable(name = "postsId")Long postid, @PathVariable(name="id")Long id, Authentication auth){
        String userName = auth.getName();
        CommentDeleteResponse response = commentService.commentDelete(postid,id,userName);
        return Response.success(response);
    }


    // ---------------------- 좋아요 ---------------------

    // 좋아요 누르기
    @ApiOperation(value = "좋아요 누르기", notes = "해당 포스트에 대한 좋아요 누름")
    @PostMapping("/{postId}/likes")
    public Response<String> postLike(@PathVariable(name = "postId")Long id, Authentication auth){
        String userName = auth.getName();
        String str = likeService.postlike(id,userName);
        return Response.success(str);
    }


    // 해당 게시물의 좋아요 총 개수 구하기
    @ApiOperation(value = "좋아요 총 개수 출력", notes = "해당 포스트에 대한 좋아요 총 개수 출력")
    @GetMapping("/{postsId}/likes")
    public Response<Integer> postLikeCount(@PathVariable(name = "postsId")Long id){
        return Response.success(likeService.postlikecount(id));
    }


    // ---------------------- 마이 피드 ---------------------
    @ApiOperation(value = "마이 피드", notes = "자신이 작성한 게시물 출력")
    @GetMapping("/my")
    public Response<Page<MypagelistResponse>> myPage(Authentication auth,Pageable pageable){
        String userName = auth.getName();
        Page<MypagelistResponse> responses = postService.mypage(userName,pageable);
        return Response.success(responses);
    }
}
