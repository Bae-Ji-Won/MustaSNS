package com.likelionfinalproject1.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelionfinalproject1.Domain.Entity.User;
import com.likelionfinalproject1.Domain.dto.User.UserJoinDto;
import com.likelionfinalproject1.Domain.dto.User.UserJoinRequest;
import com.likelionfinalproject1.Domain.dto.User.UserLoginRequest;
import com.likelionfinalproject1.Exception.AppException;
import com.likelionfinalproject1.Exception.ErrorCode;
import com.likelionfinalproject1.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRestController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean   // 가짜 객체
    UserService userService;    // UserService Bean 파일 의존
    @MockBean
    BCryptPasswordEncoder encoder;
    @Autowired
    ObjectMapper objectMapper;

    UserJoinRequest userJoinRequest;
    UserLoginRequest userLoginRequest;
    @BeforeEach         // 중복되는 코드 따로 빼내서 사용
    public void setup() {
        userJoinRequest = UserJoinRequest.builder()
                .userName("han")
                .password("1234")
                .build();

        userLoginRequest = UserLoginRequest.builder()
                .userName("han")
                .password("1234")
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    @WithMockUser
    void createUser_success() throws Exception {
        // given
        setup();

        // when
        User user = userJoinRequest.toEntity(encoder.encode(userJoinRequest.getPassword()));    // 비밀번호 암호화하여 User Entity 생성
        UserJoinDto dao = UserJoinDto.fromEntity(user);         // Entity -> Dao

        when(userService.join(any())).thenReturn(dao);

        // then
        mockMvc.perform(post("/api/v1/users/join")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                // userName 존재 여부 확인
                .andExpect(jsonPath("$..userName").exists())
                // userName의 값 비교
                .andExpect(jsonPath("$..userName").value("han"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 실패 : username 중복")
    @WithMockUser
    void createUser_fail() throws Exception {
        // given
        setup();


        // 이전에는 when/thenReturn을 통해 구현했는데 그렇게 하면 given 구역에서 when을 사용하면 헷갈릴 수 있으므로 given으로 구역을 표시하며 정확히 한다.
        // when
        User user = userJoinRequest.toEntity(encoder.encode(userJoinRequest.getPassword()));    // 비밀번호 암호화하여 User Entity 생성

        given(userService.join(any()))
                .willThrow(new AppException(ErrorCode.DUPLICATED_USER_NAME));

        // then
        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().isConflict());

        verify(userService).join(any());
    }

    @Test
    @DisplayName("로그인 실패 - password 틀림")
    @WithMockUser
    void loginPw_fail() throws Exception {
        setup();


        given(userService.login(any()))
                .willThrow(new AppException(ErrorCode.INVALID_PASSWORD));

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userJoinRequest.getUserName(), userJoinRequest.getPassword()))))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(userService).login(any());
    }

    @Test
    @DisplayName("로그인 실패 - Id 틀림")
    @WithMockUser
    void loginId_fail() throws Exception {
        setup();

        given(userService.login(any()))
                .willThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND));

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(userService).login(any());
    }

    @Test
    @DisplayName("로그인 성공")
    @WithMockUser
    void login_success () throws Exception {
        // given
        setup();

        // when
        given(userService.login(any()))
                .willReturn("token");

        // then
        mockMvc.perform(post("/api/v1/users/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).login(any());
    }
}