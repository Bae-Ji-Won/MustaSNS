package com.likelionfinalproject1.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelionfinalproject1.Domain.Entity.Comment;
import com.likelionfinalproject1.Domain.Entity.Post;
import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.dto.Comment.CommentDto;
import com.likelionfinalproject1.Domain.dto.Comment.CommentRequest;
import com.likelionfinalproject1.Domain.dto.Comment.CommentResponse;
import com.likelionfinalproject1.Domain.dto.Post.PostChangeResponse;
import com.likelionfinalproject1.Domain.dto.Post.PostCreateRequest;
import com.likelionfinalproject1.Domain.dto.Post.PostCreateResponse;
import com.likelionfinalproject1.Domain.dto.Post.PostOneResponse;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Fixture.PostEntityFixture;
import com.likelionfinalproject1.Fixture.UserEntityFixture;
import com.likelionfinalproject1.Service.CommentService;
import com.likelionfinalproject1.Service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(PostRestController.class)
class PostRestControllerTest {

    Logger log = (Logger) LoggerFactory.getLogger(PostRestControllerTest.class);    // Junit에서 log찍기 위해 선언(Junit에서는 @Slf4j 어노테이션 사용 불가능)
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean   // 가짜 객체
    PostService postService;

    @MockBean
    CommentService commentService;


    @MockBean
    BCryptPasswordEncoder encoder;



    // --------------------- 포스트 Test -----------------------

    @Test
    @DisplayName("포스트 작성 성공")
    @WithMockUser
    void createPost_success() throws Exception {
        PostCreateRequest request = new PostCreateRequest("title","body");
        PostCreateResponse postCreateResponse = PostCreateResponse.success(0l);

        given(postService.postCreate(any(),any()))
                .willReturn(postCreateResponse);

        mockMvc.perform(post("/api/v1/posts")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..message").exists())
                .andExpect(jsonPath("$..postId").exists());

        verify(postService).postCreate(any(),any());
    }

    @Test
    @DisplayName("포스트 작성 실패 - 로그인을 하지 않음")
    @WithMockUser
    void createPost_fail() throws Exception {
        PostCreateRequest request = new PostCreateRequest("title","body");
        PostCreateResponse postCreateResponse = PostCreateResponse.success(0l);

        given(postService.postCreate(any(),any()))
                .willThrow(new AppException(ErrorCode.INVALID_PERMISSION));

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(postService).postCreate(any(),any());
    }

    @Test
    @DisplayName("포스트 세부 조회 성공")
    @WithMockUser
    void readPost_success() throws Exception {

        PostOneResponse PostEntity = PostOneResponse.builder()  //
                .id(1l)
                .title("test-title")
                .body("test-body")
                .userName("han")
                .build();

        given(postService.getpostbyid(any()))
                .willReturn(PostEntity);

        mockMvc.perform(get("/api/v1/posts/1")
                   .with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$..id").value(1))  // 저장된 값 비교
                .andExpect(jsonPath("$..title").exists())
                .andExpect(jsonPath("$..body").exists())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("포스트 수정 성공")
    @WithMockUser
    void updatePost_success() throws Exception {
        PostCreateRequest ModifyRequest = new PostCreateRequest("update-title","update-body");    // 수정할 내용

        PostChangeResponse PostEntity = new PostChangeResponse("포스트 수정 완료",1l);     // Response 설정

        given(postService.postupdate(any(),any(),any()))
                .willReturn(PostEntity);

        mockMvc.perform(put("/api/v1/posts/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(ModifyRequest)))
                .andDo(print())
                .andExpect(jsonPath("$..message").exists())
                .andExpect(jsonPath("$..message").value("포스트 수정 완료"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("포스트 수정 실패(1) - 인증 실패")
    @WithAnonymousUser  // 인증이 안된 상태
    void updatePost_fail_one() throws Exception {
        PostCreateRequest ModifyRequest = new PostCreateRequest("update-title","update-body");    // 수정할 내용

        given(postService.postupdate(any(),any(),any()))
                .willThrow(new AppException(ErrorCode.INVALID_PERMISSION));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ModifyRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("포스트 수정 실패(2) - 작성자 불일치")
    @WithMockUser
    void updatePost_fail_two() throws Exception {
        PostCreateRequest ModifyRequest = new PostCreateRequest("update-title","update-body");    // 수정할 내용

        given(postService.postupdate(any(),any(),any()))
                .willThrow(new AppException(ErrorCode.INVALID_PERMISSION));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ModifyRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("포스트 수정 실패(3) - 데이터베이스 에러")
    @WithMockUser
    void updatePost_fail_three() throws Exception {
        PostCreateRequest ModifyRequest = new PostCreateRequest("update-title","update-body");    // 수정할 내용

        given(postService.postupdate(any(),any(),any()))
                .willThrow(new AppException(ErrorCode.DATABASE_ERROR));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ModifyRequest)))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }


    @Test
    @DisplayName("포스트 삭제 성공")
    @WithMockUser
    void deletePost_success() throws Exception {

        PostChangeResponse PostEntity = new PostChangeResponse("포스트 삭제 완료",1l);     // Response 설정
        
        given(postService.postdelete(any(),any()))
                .willReturn(PostEntity);

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$..message").exists())
                .andExpect(jsonPath("$..message").value("포스트 삭제 완료"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("포스트 삭제 실패(1) : 인증 실패")
    @WithAnonymousUser  // 인증이 안된 상태
    void deletePost_fail_one() throws Exception {

        given(postService.postdelete(any(),any()))
                .willThrow(new AppException(ErrorCode.INVALID_PERMISSION));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("포스트 삭제 실패(2) : 작성자 불일치")
    @WithMockUser
    void deletePost_fail_two() throws Exception {

        given(postService.postdelete(any(),any()))
                .willThrow(new AppException(ErrorCode.INVALID_PERMISSION));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("포스트 삭제 실패(3) :  데이터 베이스 에러")
    @WithMockUser
    void deletePost_fail_three() throws Exception {

        given(postService.postdelete(any(),any()))
                .willThrow(new AppException(ErrorCode.DATABASE_ERROR));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("포스트 목록 조회 성공")
    @WithMockUser
    void readlistPost_success() throws Exception {

        mockMvc.perform(get("/api/v1/posts")
                        .param("page","0")
                        .param("size","20")
                        .param("sort","createdAt,desc")
                        .with(csrf()))
                .andExpect(status().isOk());

    }


    // --------------------- 댓글 Test -----------------------
    @Test
    @DisplayName("댓글 작성 성공")
    @WithMockUser
    void createComment_Success() throws Exception {
        CommentRequest request = new CommentRequest("comment");     // 유저가 입력할 댓글 request DTO 생성

        CommentDto dto = new CommentDto(1l,request.getComment(),"han",1l,"2017");

        given(commentService.commentCreate(any(),any(),any()))
                .willReturn(dto);

        mockMvc.perform(post("/api/v1/posts/1/comments")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..id").exists())
                .andExpect(jsonPath("$..comment").exists());

        verify(commentService).commentCreate(any(),any(),any());
    }

    @Test
    @DisplayName("댓글 작성 실패(1) - 로그인 하지 않은 경우")
    @WithAnonymousUser
    void createComment_Fail_one() throws Exception {
        CommentRequest request = new CommentRequest("comment");     // 유저가 입력할 댓글 request DTO 생성

        CommentDto dto = new CommentDto(1l,request.getComment(),"han",1l,"2017");

        given(commentService.commentCreate(any(),any(),any()))
                .willThrow(new AppException(ErrorCode.INVALID_PERMISSION)); // 로그인을 한 상태가 아닌 경우 인가가 안된 경우이므로 권한이 없음

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // verify(commentService).commentCreate(any(),any(),any());  현재 로그인이 안된 상태 즉, 인가가 안된 상태이므로 Service에 대한 반환값 확인을 할 수 없음
    }

    @Test
    @DisplayName("댓글 작성 실패(2) - 게시물이 존재하지 않는 경우")
    @WithMockUser
    void createComment_Fail_two() throws Exception {
        CommentRequest request = new CommentRequest("comment");     // 유저가 입력할 댓글 request DTO 생성

        CommentDto dto = new CommentDto(1l,request.getComment(),"han",1l,"2017");

        given(commentService.commentCreate(any(),any(),any()))
                .willThrow(new AppException(ErrorCode.POST_NOT_FOUND)); // 포스트에 대한 정보를 못찾음

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isNotFound());

        // verify(commentService).commentCreate(any(),any(),any());  현재 로그인이 안된 상태 즉, 인가가 안된 상태이므로 Service에 대한 반환값 확인을 할 수 없음
    }

    @Test
    @DisplayName("댓글 수정 성공")
    @WithMockUser
    void updateComment_Success() throws Exception {
        CommentRequest request = new CommentRequest("modify-comment");     // 유저가 입력할 댓글 request DTO 생성

        User user = UserEntityFixture.userEntity("han","1234");     // DB에서 찾아온 User 데이터
        Post post = PostEntityFixture.postEntity("han","1234");     // DB에서 찾아온 Post 데이터

        Comment comment = Comment.builder()         // DB에서 가져온 기존 Comment 데이터
                .id(1l)
                .comment("orginal message")
                .post(post)
                .user(user)
                .build();

        comment.update(request.getComment());   // 데이터 업데이트

        CommentResponse response = CommentResponse.fromDto(new CommentDto().fromentity(comment));   // 업데이트 된 Commet Entity -> CommentResponse

        given(commentService.commentUpdate(any(),any(),any(),any()))
                .willReturn(response);

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..id").exists())
                .andExpect(jsonPath("$..comment").exists())
                .andExpect(jsonPath("$..comment").value("modify-comment"))      // 변경된 내용 확인
                .andExpect(jsonPath("$..userName").exists())
                .andExpect(jsonPath("$..postId").exists())
                .andExpect(jsonPath("$..createdAt").exists());

        verify(commentService).commentUpdate(any(),any(),any(),any());
    }

    @Test
    @DisplayName("댓글 수정 실패(1) - 인증 실패")
    @WithAnonymousUser
    void updateComment_Fail_one() throws Exception {
        CommentRequest request = new CommentRequest("modify-comment");     // 유저가 입력할 댓글 request DTO 생성


        given(commentService.commentUpdate(any(),any(),any(),any()))
                .willThrow(new AppException(ErrorCode.INVALID_PERMISSION));
        
        

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // verify(commentService).commentUpdate(any(),any(),any(),any());  현재 로그인이 안된 상태 즉, 인가가 안된 상태이므로 Service에 대한 반환값 확인을 할 수 없음
    }

    @Test
    @DisplayName("댓글 수정 실패(2) - 작성자 불일치")
    @WithMockUser
    void updateComment_Fail_two() throws Exception {
        CommentRequest request = new CommentRequest("modify-comment");     // 유저가 입력할 댓글 request DTO 생성

        User user = UserEntityFixture.userEntity("han","1234");     // DB에서 찾아온 User 데이터
        Post post = PostEntityFixture.postEntity("han","1234");     // DB에서 찾아온 Post 데이터

        Comment comment = Comment.builder()         // DB에서 가져온 기존 Comment 데이터
                .id(1l)
                .comment("orginal message")
                .post(post)
                .user(user)
                .build();

        CommentDto commentHost = new CommentDto().fromentity(comment);     // Comment 작성한 유저
        
        CommentDto commentCustomer = new CommentDto(1l,request.getComment(),"user",1l,"2017");  // Comment 수정할려고 하는 외부인

        log.info("commentHost.name :"+commentHost.getUserName());
        log.info("commentCustomer.name :"+commentCustomer.getUserName());

        // 포스트 작성 유저이름과 수정할려고 하는 유저의 이름이 다를경우 예외처리
        if(!commentHost.getUserName().equals(commentCustomer.getUserName())) {
            given(commentService.commentUpdate(any(), any(), any(), any()))
                    .willThrow(new AppException(ErrorCode.INVALID_PERMISSION));
        }


        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(commentService).commentUpdate(any(),any(),any(),any());
    }

    @Test
    @DisplayName("댓글 수정 실패(3) - 데이터 베이스 에러")
    @WithMockUser
    void updateComment_Fail_three() throws Exception {
        CommentRequest request = new CommentRequest("modify-comment");     // 유저가 입력할 댓글 request DTO 생성


        given(commentService.commentUpdate(any(),any(),any(),any()))
                .willThrow(new AppException(ErrorCode.DATABASE_ERROR));

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(commentService).commentUpdate(any(),any(),any(),any());
    }


    @Test
    @DisplayName("댓글 삭제 성공")
    @WithMockUser
    void deleteComment_Success() throws Exception {

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("댓글 삭제 실패(1) - 인증 실패")
    @WithAnonymousUser
    void deleteComment_Fail_one() throws Exception {
        CommentRequest request = new CommentRequest("modify-comment");     // 유저가 입력할 댓글 request DTO 생성

        given(commentService.commentDelete(any(),any(),any()))
                .willThrow(new AppException(ErrorCode.INVALID_PERMISSION));

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("댓글 삭제 실패(2) - 작성자 불일치")
    @WithMockUser
    void deleteComment_Fail_two() throws Exception {
        CommentRequest request = new CommentRequest("modify-comment");     // 유저가 입력할 댓글 request DTO 생성

        User user = UserEntityFixture.userEntity("han","1234");     // DB에서 찾아온 User 데이터
        Post post = PostEntityFixture.postEntity("han","1234");     // DB에서 찾아온 Post 데이터

        Comment comment = Comment.builder()         // DB에서 가져온 기존 Comment 데이터
                .id(1l)
                .comment("orginal message")
                .post(post)
                .user(user)
                .build();

        CommentDto commentHost = new CommentDto().fromentity(comment);     // Comment 작성한 유저

        CommentDto commentCustomer = new CommentDto(1l,request.getComment(),"user",1l,"2017");  // Comment 수정할려고 하는 외부인

        log.info("commentHost.name :"+commentHost.getUserName());
        log.info("commentCustomer.name :"+commentCustomer.getUserName());

        // 포스트 작성 유저이름과 수정할려고 하는 유저의 이름이 다를경우 예외처리
        if(!commentHost.getUserName().equals(commentCustomer.getUserName())) {
            given(commentService.commentDelete(any(), any(), any()))
                    .willThrow(new AppException(ErrorCode.INVALID_PERMISSION));
        }

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(commentService).commentDelete(any(),any(),any());
    }

    @Test
    @DisplayName("댓글 삭제 실패(3) - 데이터 베이스 에러")
    @WithMockUser
    void deleteComment_Fail_three() throws Exception {
        CommentRequest request = new CommentRequest("modify-comment");     // 유저가 입력할 댓글 request DTO 생성


        given(commentService.commentDelete(any(),any(),any()))
                .willThrow(new AppException(ErrorCode.DATABASE_ERROR));

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(commentService).commentDelete(any(),any(),any());
    }


    @Test
    @DisplayName("좋아요 누르기 성공")
    @WithMockUser
    void pushLike_Success() throws Exception {

        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("좋아요 누르기 실패(1) - 로그인 하지 않은 경우")
    @WithAnonymousUser
    void pushLike_Fail_one() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("좋아요 누르기(2) - 게시물이 존재하지 않는 경우")
    @WithMockUser
    void pushLike_Fail_two() throws Exception {
        given(postService.postlike(any(),any()))
                .willThrow(new AppException(ErrorCode.POST_NOT_FOUND));

        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("마이피드 조회 성공")
    @WithMockUser
    void mypage_Success() throws Exception {
        given(postService.mypage(any(),any()))
                .willReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts/my")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("마이피드 조회 실패 - 로그인 하지 않은 경우")
    @WithAnonymousUser
    void mypage_Fail()throws Exception{
        given(postService.mypage(any(),any()))
                .willReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts/my")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}