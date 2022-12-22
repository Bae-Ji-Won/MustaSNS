package com.likelionfinalproject1.Controller;

import com.likelionfinalproject1.Domain.Response;
import com.likelionfinalproject1.Domain.dto.*;
import com.likelionfinalproject1.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 가입
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest userRequest){
        UserJoinDao userJoinDao = userService.join(userRequest);
        return Response.success(new UserJoinResponse(userJoinDao.getId(),userJoinDao.getUsername()));
    }

    // 로그인
    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest){
        String token = userService.login(userLoginRequest);
        return Response.success(new UserLoginResponse(token));
    }
}
