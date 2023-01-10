package com.likelionfinalproject1.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelionfinalproject1.Domain.dto.Post.PostChangeResponse;
import com.likelionfinalproject1.Domain.dto.Post.PostCreateRequest;
import com.likelionfinalproject1.Domain.dto.Post.PostCreateResponse;
import com.likelionfinalproject1.Domain.dto.Post.PostOneResponse;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PostRestController.class)
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean   // 가짜 객체
    PostService postService;

    @MockBean
    BCryptPasswordEncoder encoder;


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
    @WithMockUser
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
    @WithMockUser
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

}