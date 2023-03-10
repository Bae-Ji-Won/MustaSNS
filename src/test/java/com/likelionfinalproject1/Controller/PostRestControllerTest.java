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
import com.likelionfinalproject1.Service.LikeService;
import com.likelionfinalproject1.Service.PostService;
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

    Logger log = (Logger) LoggerFactory.getLogger(PostRestControllerTest.class);    // Junit?????? log?????? ?????? ??????(Junit????????? @Slf4j ??????????????? ?????? ?????????)
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean   // ?????? ??????
    PostService postService;

    @MockBean
    LikeService likeService;

    @MockBean
    CommentService commentService;


    @MockBean
    BCryptPasswordEncoder encoder;



    // --------------------- ????????? Test -----------------------

    @Test
    @DisplayName("????????? ?????? ??????")
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
    @DisplayName("????????? ?????? ?????? - ???????????? ?????? ??????")
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
    @DisplayName("????????? ?????? ?????? ??????")
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
                .andExpect(jsonPath("$..id").value(1))  // ????????? ??? ??????
                .andExpect(jsonPath("$..title").exists())
                .andExpect(jsonPath("$..body").exists())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    @WithMockUser
    void updatePost_success() throws Exception {
        PostCreateRequest ModifyRequest = new PostCreateRequest("update-title","update-body");    // ????????? ??????

        PostChangeResponse PostEntity = new PostChangeResponse("????????? ?????? ??????",1l);     // Response ??????

        given(postService.postupdate(any(),any(),any()))
                .willReturn(PostEntity);

        mockMvc.perform(put("/api/v1/posts/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(ModifyRequest)))
                .andDo(print())
                .andExpect(jsonPath("$..message").exists())
                .andExpect(jsonPath("$..message").value("????????? ?????? ??????"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("????????? ?????? ??????(1) - ?????? ??????")
    @WithAnonymousUser  // ????????? ?????? ??????
    void updatePost_fail_one() throws Exception {
        PostCreateRequest ModifyRequest = new PostCreateRequest("update-title","update-body");    // ????????? ??????

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
    @DisplayName("????????? ?????? ??????(2) - ????????? ?????????")
    @WithMockUser
    void updatePost_fail_two() throws Exception {
        PostCreateRequest ModifyRequest = new PostCreateRequest("update-title","update-body");    // ????????? ??????

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
    @DisplayName("????????? ?????? ??????(3) - ?????????????????? ??????")
    @WithMockUser
    void updatePost_fail_three() throws Exception {
        PostCreateRequest ModifyRequest = new PostCreateRequest("update-title","update-body");    // ????????? ??????

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
    @DisplayName("????????? ?????? ??????")
    @WithMockUser
    void deletePost_success() throws Exception {

        PostChangeResponse PostEntity = new PostChangeResponse("????????? ?????? ??????",1l);     // Response ??????
        
        given(postService.postdelete(any(),any()))
                .willReturn(PostEntity);

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$..message").exists())
                .andExpect(jsonPath("$..message").value("????????? ?????? ??????"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("????????? ?????? ??????(1) : ?????? ??????")
    @WithAnonymousUser  // ????????? ?????? ??????
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
    @DisplayName("????????? ?????? ??????(2) : ????????? ?????????")
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
    @DisplayName("????????? ?????? ??????(3) :  ????????? ????????? ??????")
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
    @DisplayName("????????? ?????? ?????? ??????")
    @WithMockUser
    void readlistPost_success() throws Exception {

        mockMvc.perform(get("/api/v1/posts")
                        .param("page","0")
                        .param("size","20")
                        .param("sort","createdAt,desc")
                        .with(csrf()))
                .andExpect(status().isOk());

    }


    // --------------------- ?????? Test -----------------------
    @Test
    @DisplayName("?????? ?????? ??????")
    @WithMockUser
    void createComment_Success() throws Exception {
        CommentRequest request = new CommentRequest("comment");     // ????????? ????????? ?????? request DTO ??????

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
    @DisplayName("?????? ?????? ??????(1) - ????????? ?????? ?????? ??????")
    @WithAnonymousUser
    void createComment_Fail_one() throws Exception {
        CommentRequest request = new CommentRequest("comment");     // ????????? ????????? ?????? request DTO ??????

        CommentDto dto = new CommentDto(1l,request.getComment(),"han",1l,"2017");

        given(commentService.commentCreate(any(),any(),any()))
                .willThrow(new AppException(ErrorCode.INVALID_PERMISSION)); // ???????????? ??? ????????? ?????? ?????? ????????? ?????? ??????????????? ????????? ??????

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // verify(commentService).commentCreate(any(),any(),any());  ?????? ???????????? ?????? ?????? ???, ????????? ?????? ??????????????? Service??? ?????? ????????? ????????? ??? ??? ??????
    }

    @Test
    @DisplayName("?????? ?????? ??????(2) - ???????????? ???????????? ?????? ??????")
    @WithMockUser
    void createComment_Fail_two() throws Exception {
        CommentRequest request = new CommentRequest("comment");     // ????????? ????????? ?????? request DTO ??????

        CommentDto dto = new CommentDto(1l,request.getComment(),"han",1l,"2017");

        given(commentService.commentCreate(any(),any(),any()))
                .willThrow(new AppException(ErrorCode.POST_NOT_FOUND)); // ???????????? ?????? ????????? ?????????

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isNotFound());

        // verify(commentService).commentCreate(any(),any(),any());  ?????? ???????????? ?????? ?????? ???, ????????? ?????? ??????????????? Service??? ?????? ????????? ????????? ??? ??? ??????
    }

    @Test
    @DisplayName("?????? ?????? ??????")
    @WithMockUser
    void updateComment_Success() throws Exception {
        CommentRequest request = new CommentRequest("modify-comment");     // ????????? ????????? ?????? request DTO ??????

        User user = UserEntityFixture.userEntity("han","1234");     // DB?????? ????????? User ?????????
        Post post = PostEntityFixture.postEntity("han","1234");     // DB?????? ????????? Post ?????????

        Comment comment = Comment.builder()         // DB?????? ????????? ?????? Comment ?????????
                .id(1l)
                .comment("orginal message")
                .post(post)
                .user(user)
                .build();

        comment.update(request.getComment());   // ????????? ????????????

        CommentResponse response = CommentResponse.fromDto(new CommentDto().fromentity(comment));   // ???????????? ??? Commet Entity -> CommentResponse

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
                .andExpect(jsonPath("$..comment").value("modify-comment"))      // ????????? ?????? ??????
                .andExpect(jsonPath("$..userName").exists())
                .andExpect(jsonPath("$..postId").exists())
                .andExpect(jsonPath("$..createdAt").exists());

        verify(commentService).commentUpdate(any(),any(),any(),any());
    }

    @Test
    @DisplayName("?????? ?????? ??????(1) - ?????? ??????")
    @WithAnonymousUser
    void updateComment_Fail_one() throws Exception {
        CommentRequest request = new CommentRequest("modify-comment");     // ????????? ????????? ?????? request DTO ??????


        given(commentService.commentUpdate(any(),any(),any(),any()))
                .willThrow(new AppException(ErrorCode.INVALID_PERMISSION));
        
        

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // verify(commentService).commentUpdate(any(),any(),any(),any());  ?????? ???????????? ?????? ?????? ???, ????????? ?????? ??????????????? Service??? ?????? ????????? ????????? ??? ??? ??????
    }

    @Test
    @DisplayName("?????? ?????? ??????(2) - ????????? ?????????")
    @WithMockUser
    void updateComment_Fail_two() throws Exception {
        CommentRequest request = new CommentRequest("modify-comment");     // ????????? ????????? ?????? request DTO ??????

        User user = UserEntityFixture.userEntity("han","1234");     // DB?????? ????????? User ?????????
        Post post = PostEntityFixture.postEntity("han","1234");     // DB?????? ????????? Post ?????????

        Comment comment = Comment.builder()         // DB?????? ????????? ?????? Comment ?????????
                .id(1l)
                .comment("orginal message")
                .post(post)
                .user(user)
                .build();

        CommentDto commentHost = new CommentDto().fromentity(comment);     // Comment ????????? ??????
        
        CommentDto commentCustomer = new CommentDto(1l,request.getComment(),"user",1l,"2017");  // Comment ??????????????? ?????? ?????????

        log.info("commentHost.name :"+commentHost.getUserName());
        log.info("commentCustomer.name :"+commentCustomer.getUserName());

        // ????????? ?????? ??????????????? ??????????????? ?????? ????????? ????????? ???????????? ????????????
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
    @DisplayName("?????? ?????? ??????(3) - ????????? ????????? ??????")
    @WithMockUser
    void updateComment_Fail_three() throws Exception {
        CommentRequest request = new CommentRequest("modify-comment");     // ????????? ????????? ?????? request DTO ??????


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
    @DisplayName("?????? ?????? ??????")
    @WithMockUser
    void deleteComment_Success() throws Exception {

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("?????? ?????? ??????(1) - ?????? ??????")
    @WithAnonymousUser
    void deleteComment_Fail_one() throws Exception {
        CommentRequest request = new CommentRequest("modify-comment");     // ????????? ????????? ?????? request DTO ??????

        given(commentService.commentDelete(any(),any(),any()))
                .willThrow(new AppException(ErrorCode.INVALID_PERMISSION));

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("?????? ?????? ??????(2) - ????????? ?????????")
    @WithMockUser
    void deleteComment_Fail_two() throws Exception {
        CommentRequest request = new CommentRequest("modify-comment");     // ????????? ????????? ?????? request DTO ??????

        User user = UserEntityFixture.userEntity("han","1234");     // DB?????? ????????? User ?????????
        Post post = PostEntityFixture.postEntity("han","1234");     // DB?????? ????????? Post ?????????

        Comment comment = Comment.builder()         // DB?????? ????????? ?????? Comment ?????????
                .id(1l)
                .comment("orginal message")
                .post(post)
                .user(user)
                .build();

        CommentDto commentHost = new CommentDto().fromentity(comment);     // Comment ????????? ??????

        CommentDto commentCustomer = new CommentDto(1l,request.getComment(),"user",1l,"2017");  // Comment ??????????????? ?????? ?????????

        log.info("commentHost.name :"+commentHost.getUserName());
        log.info("commentCustomer.name :"+commentCustomer.getUserName());

        // ????????? ?????? ??????????????? ??????????????? ?????? ????????? ????????? ???????????? ????????????
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
    @DisplayName("?????? ?????? ??????(3) - ????????? ????????? ??????")
    @WithMockUser
    void deleteComment_Fail_three() throws Exception {
        CommentRequest request = new CommentRequest("modify-comment");     // ????????? ????????? ?????? request DTO ??????


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
    @DisplayName("????????? ????????? ??????")
    @WithMockUser
    void pushLike_Success() throws Exception {

        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("????????? ????????? ??????(1) - ????????? ?????? ?????? ??????")
    @WithAnonymousUser
    void pushLike_Fail_one() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("????????? ?????????(2) - ???????????? ???????????? ?????? ??????")
    @WithMockUser
    void pushLike_Fail_two() throws Exception {
        given(likeService.postlike(any(),any()))
                .willThrow(new AppException(ErrorCode.POST_NOT_FOUND));

        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("???????????? ?????? ??????")
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
    @DisplayName("???????????? ?????? ?????? - ????????? ?????? ?????? ??????")
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